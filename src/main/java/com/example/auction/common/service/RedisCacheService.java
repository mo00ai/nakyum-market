package com.example.auction.common.service;

import static com.example.auction.common.util.TimeRangeUtils.*;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheService {

	private final RedisService redisService;

	private static final Duration SEARCH_LOG_TTL = Duration.ofMinutes(20);
	private static final Duration TOP_KEYWORDS_TTL = Duration.ofMinutes(10);

	// 검색기록 저장 (ZSET)
	public void saveSearchLog(String keyword, Long now) {

		String blockKey = "search_logs:" + getCurrentBlockKey(now);
		String value = keyword + ":" + UUID.randomUUID();

		redisService.setZSetValue(blockKey, value, now);
		redisService.expireKey(blockKey, SEARCH_LOG_TTL);
	}

	// 인기 검색어 계산 및 캐싱
	public List<String> saveTopKeywords() {
		long now = System.currentTimeMillis();
		TimeRange range = getPreviousTimeBlock(now);

		String zsetKey = "search_logs:" + getCurrentBlockKey(range.start);
		String cacheKey = "top10:" + getCurrentBlockKey(now);  // 현재 시각 기준 캐시

		Set<Object> logsByRange = redisService.getZSetRangeByScore(zsetKey, range.start, range.end);

		Map<String, Long> keywordCounts = logsByRange.stream()
			.map(Object::toString)
			.map(val -> val.split(":")[0])  // keyword 추출
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		List<String> topKeywords = keywordCounts.entrySet().stream()
			.sorted(Map.Entry.<String, Long>comparingByValue().reversed())
			.limit(10)
			.map(Map.Entry::getKey)
			.toList();

		redisService.setKeyList(cacheKey, topKeywords, TOP_KEYWORDS_TTL);

		return topKeywords;
	}

	// 인기 검색어 조회 (캐시 miss 시 계산)
	public synchronized List<String> getTopKeywords() {
		long now = System.currentTimeMillis();
		String cacheKey = "top10:" + getCurrentBlockKey(now);

		List<String> cached = Optional.ofNullable(redisService.getKeyStrings(cacheKey))
			.orElse(Collections.emptyList());

		if (!cached.isEmpty()) {
			return cached;
		}

		return saveTopKeywords();
	}

}
