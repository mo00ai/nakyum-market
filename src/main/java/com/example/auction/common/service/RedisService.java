package com.example.auction.common.service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

	private final RedisTemplate<String, Object> redisTemplate;

	public RedisService(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
	 * 사용자가 필요한 set 메서드가 더 있다면 직접 만들어서 사용하세요
	 */

	// key, String
	public void setKeyValue(String key, Object value) {
		redisTemplate.opsForValue().set(key, value);
	}

	// key, String, Duration
	public void setKeyValue(String key, String value, Duration validityTime) {
		redisTemplate.opsForValue().set(key, value, validityTime);
	}

	// key, Object, Duration
	public void setKeyValue(String key, Object value, Duration validityTime) {
		redisTemplate.opsForValue().set(key, value, validityTime);
	}

	// key, Long, Duration
	public void setKeyValue(String key, Long value, Duration validityTime) {
		redisTemplate.opsForValue().set(key, value, validityTime);
	}

	// key, List<String>, Duration
	public void setKeyValues(String key, List<String> values, Duration validityTime) {
		redisTemplate.opsForValue().set(key, values, validityTime);
	}

	public void setZSetValue(String key, String value, double score) {
		redisTemplate.opsForZSet().add(key, value, score);
	}

	public Set<Object> getZSetRangeByScore(String key, double min, double max) {
		return redisTemplate.opsForZSet().rangeByScore(key, min, max);
	}

	/**
	 * 사용자가 필요한 get 메서드가 더 있다면 직접 만들어서 사용하세요(형변환 등)
	 */

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

	public List<?> getKeyValues(String key) {
		Object object = redisTemplate.opsForValue().get(key);

		if (object instanceof List<?> result) {
			return result;
		}

		return Collections.emptyList();
	}

	// ZSET에서 특정 시간 범위(score에 시간을 저장)로 검색
	public Set<Object> getZSetRange(String key, Long start, Long end) {
		return redisTemplate.opsForZSet().rangeByScore(key, start, end);
	}

	public Long incrementValue(String key) {
		return redisTemplate.opsForValue().increment(key);
	}

	public void deleteKeyValue(String key) {
		redisTemplate.delete(key);
	}

	// ZSET 전체에 TTL 설정
	public void expireKey(String key, Duration validityTime) {
		redisTemplate.expire(key, validityTime);
	}

}
