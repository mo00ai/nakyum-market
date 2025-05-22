package com.example.auction.common.service;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.Set;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.ScanOptions;
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
	// ZSET 전체에 TTL 설정
	public void expireKey(String key, Duration validityTime) {
		redisTemplate.expire(key, validityTime);
	}

	public Long getExpire(String key) {
		return redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}

}
