package com.example.auction.domain.auctionbid.handler;

import static com.example.auction.domain.user.exception.ErrorCode.NOT_FOUND_USER;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;

import com.example.auction.common.exception.CustomException;
import com.example.auction.common.exception.ErrorCode;
import com.example.auction.common.handler.RedisKeyEventHandler;
import com.example.auction.common.service.RedisService;
import com.example.auction.domain.auctionbid.dto.BidRedisDto;
import com.example.auction.domain.auctionbid.exception.AuctionBidErrorCode;
import com.example.auction.domain.product.service.ProductService;
import com.example.auction.domain.test.repository.AuctionBidJdbcRepository;
import com.example.auction.domain.user.entity.User;
import com.example.auction.domain.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

@Component
@RequiredArgsConstructor
public class AuctionBidKeyEventHandler implements RedisKeyEventHandler {

	private final RedisService redisService;
	private final ProductService productService;
	private final AuctionBidJdbcRepository auctionBidJdbcRepository;
	private final UserRepository userRepository;

	@Override
	public String getPrefix() {
		return "auction:end";
	}

	//redis 데이터 세팅에 따라 수정 예정
	@Override
	public void handle(String key) {

		String productId = key.split("auction:end:")[1];

		String getLogs = "auction:" + productId + ":logs";
		String getHighest = "auction:" + productId + ":highest";

		setBatchInsert(getLogs);
		setFinalPrice(getHighest);
	}

	public void setBatchInsert(String key) {
		Set<TypedTuple<Object>> tuples = redisService.getZSetReversData(key);
		if (tuples == null || tuples.isEmpty()) {
			throw new CustomException(AuctionBidErrorCode.REDIS_LOGS_NOT_FOUND);
		}
		List<String> jsonList = tuples.stream().map(data -> (String)data.getValue()).toList();
		List<BidRedisDto> dtoList = jsonList.stream().map(json -> {
			try {
				return setObjectMapper().readValue(json, BidRedisDto.class);
			} catch (JsonProcessingException e) {
				throw new RuntimeException(e);
			}
		}).toList();
		auctionBidJdbcRepository.batchInsert(dtoList);
		redisService.deleteRedisTemplateKeyValue(key);
	}

	public void setFinalPrice(String key) {
		String json = redisService.getKeyValueAsString(key);
		BidRedisDto dto;
		try {
			dto = setObjectMapper().readValue(json, BidRedisDto.class);
		} catch (JsonProcessingException e) {
			throw new CustomException(ErrorCode.REDIS_DESERIALIZATION_ERROR);
		}
		User user = userRepository.findById(dto.getUserId()).orElseThrow(() -> new CustomException(NOT_FOUND_USER));
		productService.updateFinalPrice(dto.getProductId(), dto.getBidPrice(), user);
		redisService.deleteStringRedisTemplateKeyValue(key);

	}

	public ObjectMapper setObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addDeserializer(LocalDateTime.class,
			new LocalDateTimeDeserializer(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
		objectMapper.registerModule(javaTimeModule);
		objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return objectMapper;
	}

}
