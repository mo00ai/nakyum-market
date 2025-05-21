package com.example.auction.common.service;

import java.time.Duration;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
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

	public Long getExpire(String key) {
		return redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}

	public Long incrementValue(String key) {
		return redisTemplate.opsForValue().increment(key);
	}

	public void deleteKeyValue(String key) {
		redisTemplate.delete(key);
	}

}
