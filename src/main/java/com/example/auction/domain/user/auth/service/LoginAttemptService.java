package com.example.auction.domain.user.auth.service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginAttemptService {
	private final StringRedisTemplate redisTemplate;

	private static final int MAX_ATTEMPTS = 5;
	private static final Duration LOCK_TIME = Duration.ofMinutes(5);

	private String key(String email) {
		return "login:fail" + email;
	}

	public void loginFailed(String email) {
		String key = key(email);
		Long failCount = redisTemplate.opsForValue().increment(key);

		if (failCount != null && failCount == 1L) {
			redisTemplate.expire(key, LOCK_TIME);        //첫 실패때 TTL 설정

		}

	}

	public boolean isLocked(String email) {
		String key = key(email);
		String count = redisTemplate.opsForValue().get(key);

		return count != null && Integer.parseInt(count) >= MAX_ATTEMPTS;
	}

	public void resetFailCount(String email) {
		redisTemplate.delete(key(email));
	}

	public Duration getRemainingLockTime(String email) {
		String key = key(email);
		Long seconds = redisTemplate.getExpire(key, TimeUnit.SECONDS);//양수 - 남은 ttl  -1:ttl 없음 -2: 존재하지 않는 키
		if (seconds < 0) {
			return Duration.ZERO;
		}
		return Duration.ofSeconds(seconds);
	}
}
