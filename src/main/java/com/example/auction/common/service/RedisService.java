package com.example.auction.common.service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import java.util.Objects;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

	private final RedisTemplate<String, Object> redisTemplate;

	public RedisService(RedisTemplate<String, Object> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public RedisConnection getRedisConnection(){
		return Objects.requireNonNull(redisTemplate.getConnectionFactory()).getConnection();
	}

	/**
	 * 사용자가 필요한 set 메서드가 더 있다면 직접 만들어서 사용하세요
	 */

	// 키가 없으면 값을 설정하고 true 반환, 키가 이미 존재하면 아무 작업도 하지 않고 false 반환
	public boolean setIfAbsent(String key, Object value,  Duration validityTime) {
		return Boolean.TRUE.equals(
            redisTemplate.opsForValue().setIfAbsent(key, value, validityTime));
	}

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

	/**
	 * 사용자가 필요한 get 메서드가 더 있다면 직접 만들어서 사용하세요(형변환 등)
	 */

	public Object getKeyValue(String key) {
		return redisTemplate.opsForValue().get(key);
	}


	public Long getKeyLongValue(String key){
		Object object = redisTemplate.opsForValue().get(key);
		if (object instanceof Number) {
			return ((Number) object).longValue();
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

	public Long incrementValue(String key){
		return redisTemplate.opsForValue().increment(key);
	}


	public void setProductCntExpire(String key){
		// 현재 TTL 확인
		long currentTtlSeconds = redisTemplate.getExpire(key);
		// 10분보다 작으면 연장
		if (currentTtlSeconds > 0 && currentTtlSeconds < 600) {
			redisTemplate.expire(key, Duration.ofMinutes(10));
		}
	}


	public void deleteKeyValue(String key) {
		redisTemplate.delete(key);
	}

}
