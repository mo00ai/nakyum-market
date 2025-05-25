package com.example.auction.common.service;

import static com.example.auction.common.util.TimeRangeUtils.TimeRange;
import static com.example.auction.common.util.TimeRangeUtils.getCurrentBlockKey;
import static com.example.auction.common.util.TimeRangeUtils.getPreviousTimeBlock;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCacheService {

	private final RedisService redisService;

	private static final Duration SEARCH_LOG_TTL = Duration.ofMinutes(10);
	private static final Duration TOP_KEYWORDS_TTL = Duration.ofMinutes(10);

	public void saveSearchLog(String keyword, Long now) {

		String blockKey = "search_logs:" + getCurrentBlockKey(now);
		String value = keyword + ":" + UUID.randomUUID();

		redisService.setZSetValue(blockKey, value, now);
		redisService.expireKey(blockKey, SEARCH_LOG_TTL);
	}

	public List<String> saveTopKeywords() {
		long now = System.currentTimeMillis();
		TimeRange range = getPreviousTimeBlock(now);

		String zSetKey = "search_logs:" + getCurrentBlockKey(range.start);
		String cacheKey = "top10:" + getCurrentBlockKey(now);

		Set<Object> logsByRange = redisService.getZSetRangeByScore(zSetKey, range.start, range.end);

		Map<String, Long> keywordCounts = logsByRange.stream()
			.map(Object::toString)
			.map(val -> val.split(":")[0])
			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

		List<String> topKeywords = keywordCounts.entrySet().stream()
			.sorted(Map.Entry.<String, Long>comparingByValue().reversed())
			.limit(10)
			.map(Map.Entry::getKey)
			.toList();

		redisService.setKeyList(cacheKey, topKeywords, TOP_KEYWORDS_TTL);

		return topKeywords;
	}

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
