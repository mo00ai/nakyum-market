package com.example.auction.common.service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

	private final RedisTemplate<String, Object> redisTemplate;
	private final RedisTemplate<String, String> stringRedisTemplate;

	public RedisService(RedisTemplate<String, Object> redisTemplate,
		RedisTemplate<String, String> stringRedisTemplate) {
		this.redisTemplate = redisTemplate;
		this.stringRedisTemplate = stringRedisTemplate;
	}

	/**
	 * 사용자가 필요한 set 메서드가 더 있다면 직접 만들어서 사용하세요
	 */

	// key, String, Duration
	public void setKeyValue(String key, String value, Duration validityTime) {
		redisTemplate.opsForValue().set(key, value, validityTime);
	}

	// key, Object, Duration
	public void setKeyValue(String key, Object value, Duration validityTime) {
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

	public List<?> getKeyValues(String key) {
		Object object = redisTemplate.opsForValue().get(key);

		if (object instanceof List<?> result) {
			return result;
		}

		return Collections.emptyList();
	}

	public void deleteKeyValue(String key) {
		redisTemplate.delete(key);
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

	public void addToZSet(String key, String value, double score) {
		redisTemplate.opsForZSet().add(key, value, score);
	}
}
