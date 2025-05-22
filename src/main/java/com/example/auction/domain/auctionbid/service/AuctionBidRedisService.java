package com.example.auction.domain.auctionbid.service;

import java.util.Collections;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import com.example.auction.common.exception.CustomException;
import com.example.auction.common.exception.ErrorCode;
import com.example.auction.common.service.RedisService;
import com.example.auction.domain.auctionbid.dto.BidRedisDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionBidRedisService {

	private final RedisService redisService;
	private final ObjectMapper objectMapper;

	// 최고 입찰 정보가 저장될 키
	private String getHighestKey(Long productId) {
		return "auction:" + productId + ":highest";
	}

	// 입찰 로그가 저장될 키
	private String getLogKey(Long productId) {
		return "auction:" + productId + ":logs";
	}

	/**
	 * 입찰 정보를 Redis에 저장. 최고가보다 높은 경우만 저장됨.
	 * 최고가 저장은 Lua 스크립트를 통해 동시성 제어 및 원자성 보장
	 */

	public void trySaveHighestBid(Long productId, BidRedisDto bidRedisDto) {
		String highestKey = getHighestKey(productId);
		String logKey = getLogKey(productId);

		String json = serializeBid(bidRedisDto); // 1. 직렬화
		redisService.addToZSet(logKey, json, System.currentTimeMillis()); // 2. 로그 기록

		saveHighestBid(highestKey, bidRedisDto.getBidPrice(), json); // 3. 조건부 저장
	}

	private String serializeBid(BidRedisDto dto) {
		try {
			return objectMapper.writeValueAsString(dto);
		} catch (JsonProcessingException e) {
			throw new CustomException(ErrorCode.REDIS_SERIALIZATION_ERROR);
		}
	}

	private void saveHighestBid(String highestKey, Long bidPrice, String json) {
		String luaScript = getBidLuaScript();
		Long result = redisService.executeLuaScript(
			luaScript,
			Collections.singletonList(highestKey),
			String.valueOf(bidPrice),
			json
		);

		if (result == null) {
			throw new CustomException(ErrorCode.REDIS_OPERATION_FAILED);
		}

		if (result == 0L) {
			log.warn("입찰 실패: 새 입찰가 {} <= 기존 입찰가", bidPrice);
			throw new CustomException(ErrorCode.BID_PRICE_BELOW_HIGHEST);
		}
	}

	private String getBidLuaScript() {
		return """
				--[[
				  입찰 Lua 스크립트
				  KEYS[1]: 최고 입찰 저장 키
				  ARGV[1]: 새로운 입찰가 (문자열)
				  ARGV[2]: 새로운 입찰 정보 JSON 문자열
				--]]
			
				local existing = redis.call('GET', KEYS[1])
				redis.log(redis.LOG_NOTICE, "# 기존 입찰 JSON 문자열: " .. tostring(existing))
			
				-- 기존 값이 있다면 파싱 및 비교
				if existing then
				  local status, existingDto = pcall(cjson.decode, existing)
				  redis.log(redis.LOG_NOTICE, "# cjson.decode 성공 여부: " .. tostring(status))
				  redis.log(redis.LOG_NOTICE, "# 디코딩된 객체: " .. tostring(existingDto))
			
				  if status and existingDto and existingDto.bidPrice then
				    local currentPrice = tonumber(existingDto.bidPrice)
				    local newBidPrice = tonumber(ARGV[1])
			
				    redis.log(redis.LOG_NOTICE, "# 기존 입찰가 (숫자형): " .. tostring(currentPrice))
				    redis.log(redis.LOG_NOTICE, "# 새 입찰가 (숫자형): " .. tostring(newBidPrice))
			
				    if newBidPrice and currentPrice and newBidPrice <= currentPrice then
				      redis.log(redis.LOG_NOTICE, "# 입찰가가 낮음 → 저장하지 않음.")
				      return 0
				    end
				  else
				    redis.log(redis.LOG_NOTICE, "# JSON 파싱 실패 또는 bidPrice 없음")
				  end
				else
				  redis.log(redis.LOG_NOTICE, "# 기존 입찰 정보 없음 → 최초 저장")
				end
			
				-- 조건 통과한 경우 저장
				redis.log(redis.LOG_NOTICE, "# 입찰 정보 저장: " .. ARGV[2])
				redis.call('SET', KEYS[1], ARGV[2])
				return 1
			""";
	}

	/**
	 * 현재 Redis에 저장된 최고 입찰 정보를 반환
	 */
	public Optional<BidRedisDto> getHighestBid(Long productId) {
		String key = "auction:" + productId + ":highest";
		String json = redisService.getKeyValueAsString(key);

		if (json == null) {
			return Optional.empty();
		}
		try {
			BidRedisDto dto = objectMapper.readValue(json, BidRedisDto.class);
			return Optional.of(dto);
		} catch (JsonProcessingException e) {
			throw new CustomException(ErrorCode.REDIS_DESERIALIZATION_ERROR);
		}
	}

	// 테스트 코드 실행 시 사용되는 메서드로 전체 캐시를 지워 테스트 초기화를 위해 사용됩니다.
	public void clearKey(Long productId) {
		String logKey = "auction:" + productId + ":logs";
		String highestKey = "auction:" + productId + ":highest";
		redisService.deleteKeyValue(logKey);
		redisService.deleteKeyValue(highestKey);
	}
}
