package com.example.auction.common.service;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Slf4j
@Service
public class RedisService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final RedisTemplate<String, String> stringRedisTemplate;
	// ZSet기반 입찰 관련 DI
	// private final ObjectMapper objectMapper;

	public RedisService(RedisTemplate<String, Object> redisTemplate,
		RedisTemplate<String, String> stringRedisTemplate,
		ObjectMapper objectMapper) {
		this.redisTemplate = redisTemplate;
		this.stringRedisTemplate = stringRedisTemplate;
		// ZSet기반 입찰 관련 DI
		// this.objectMapper = objectMapper;
	}

	public RedisConnection getRedisConnection() {
		return Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection();
	}

	public boolean setIfAbsent(String key, Object value, Duration validityTime) {
		return Boolean.TRUE.equals(
			redisTemplate.opsForValue().setIfAbsent(key, value, validityTime));
	}

	public void setKeyValue(String key, String value, Duration validityTime) {
		redisTemplate.opsForValue().set(key, value, validityTime);
	}

	public void setKeyValue(String key, Object value, Duration validityTime) {
		redisTemplate.opsForValue().set(key, value, validityTime);
	}

	public void setKeyValue(String key, Long value, Duration validityTime) {
		redisTemplate.opsForValue().set(key, value, validityTime);
	}

	public void setZSetValue(String key, String value, Long score) {
		redisTemplate.opsForZSet().add(key, value, score);
	}

	public void setKeyList(String key, List<String> values, Duration ttl) {
		stringRedisTemplate.opsForList().rightPushAll(key, values);
		stringRedisTemplate.expire(key, ttl);
	}

	public Object getKeyValue(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public Long getKeyLongValue(String key) {
		Object object = redisTemplate.opsForValue().get(key);
		if (object instanceof Number) {
			return ((Number)object).longValue();
		}
		return null;
	}

	public List<String> getKeyStrings(String key) {
		List<String> cached = stringRedisTemplate.opsForList().range(key, 0, -1);
		return cached != null ? cached : Collections.emptyList();
	}

	public Set<Object> getZSetRangeByScore(String key, Long min, Long max) {
		return redisTemplate.opsForZSet().rangeByScore(key, min, max);
	}

	// getKeys는 O(n)의 성능이므로 redis에 key가 천개만 넘어가도 scan 방식으로 변경해야 한다고함
	public Set<String> scanKeys(String pattern) {
		Set<String> keys = new HashSet<>();

		ScanOptions options = ScanOptions.scanOptions().match(pattern).count(1000).build();

		RedisConnectionFactory factory = redisTemplate.getConnectionFactory();
		if (factory == null) {
			return keys;
		}

		try (RedisConnection connection = factory.getConnection();
			 Cursor<byte[]> cursor = connection.keyCommands().scan(options)) {
			while (cursor.hasNext()) {
				String key = redisTemplate.getStringSerializer().deserialize(cursor.next());
				if (key != null) {
					keys.add(key);
				}
			}
		}
		return keys;
	}

	public Long incrementValue(String key) {
		return redisTemplate.opsForValue().increment(key);
	}

	public void setProductCntExpire(String key) {
		// 현재 TTL 확인
		long currentTtlSeconds = redisTemplate.getExpire(key);
		// 10분보다 작으면 연장
		if (currentTtlSeconds > 0 && currentTtlSeconds < 600) {
			redisTemplate.expire(key, Duration.ofMinutes(10));
		}
	}

	public Set<TypedTuple<Object>> getZSetReversData(String key) {
		return redisTemplate.opsForZSet().reverseRangeWithScores(key, 0, -1);
	}

	public Set<ZSetOperations.TypedTuple<String>> getZSetRangeByScoreTuple(String key, Long min, Long max) {
		return stringRedisTemplate.opsForZSet().rangeWithScores(key, min, max);
	}

	public void deleteRedisTemplateKeyValue(String key) {
		redisTemplate.delete(key);
	}

	public void deleteStringRedisTemplateKeyValue(String key) {
		stringRedisTemplate.delete(key);
	}

	//주어진 Lua 스크립트를 Redis에서 실행하고 결과를 Long으로 반환합니다.
	public Long executeLuaScript(String script, List<String> keys, String value, String jason) {
		DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
		redisScript.setScriptText(script);           // 스크립트 내용 설정
		redisScript.setResultType(Long.class);       // 반환 타입 설정 (반드시 스크립트에서 숫자 반환해야 함)
		return stringRedisTemplate.execute(redisScript, keys, value, jason); // Redis에 스크립트 실행 요청
	}

	public String getKeyValueAsString(String key) {
		return stringRedisTemplate.opsForValue().get(key);
	}

	public void addToZSet(String key, String value, long score) {
		redisTemplate.opsForZSet().add(key, value, score);
	}

	public void expireKey(String key, Duration validityTime) {
		redisTemplate.expire(key, validityTime);
	}

	public Long getExpire(String key) {
		return redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}

	public void setOpsForSet(String key, Long id) {
		redisTemplate.opsForSet().add(key, id);
	}

	public void removeOpsForSetALL(String key) {
		redisTemplate.delete(key);
	}

	public void removeOpsForSet(String key, Long id) {
		redisTemplate.opsForSet().remove(key, id);
	}

	public Boolean isOpsForSet(String key, Long id) {
		return redisTemplate.opsForSet().isMember(key, id);
	}

	public boolean hasKey(String key) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}

	public long getSetSize(String key) {
		Long size = redisTemplate.opsForSet().size(key);
		return size != null ? size : 0;
	}

	public Set<Object> findOpsForSet(String key) {
		return redisTemplate.opsForSet().members(key);
	}

	// Zset기반 입찰 조회용 메서드
	// public BidRedisDto getZSetHighestBid(String zsetKey) {
	// 	// ZREVRANGE는 점수가 높은 순으로 반환 (0, 0): 가장 높은 점수 1개
	// 	Set<Object> result = redisTemplate.opsForZSet().reverseRange(zsetKey, 0, 0);
	//
	// 	if (result == null || result.isEmpty()) {
	// 		return null;
	// 	}
	//
	// 	Object raw = result.iterator().next();
	// 	return objectMapper.convertValue(raw, BidRedisDto.class); // 최고 입찰 JSON 문자열 반환
	// }

	// ZSet기반 입찰 저장용 메서드
	// public void addToZSetObject(String key, Object value, long score) {
	// 	redisTemplate.opsForZSet().add(key, value, score);
	// }

}
