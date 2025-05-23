// package com.example.auction.common.service;
//
// import static com.example.auction.common.util.TimeRangeUtils.*;
//
// import java.util.List;
// import java.util.Map;
// import java.util.UUID;
// import java.util.function.Function;
// import java.util.stream.Collectors;
//
// import org.springframework.beans.factory.annotation.Qualifier;
// import org.springframework.stereotype.Service;
//
// import com.example.auction.common.util.TimeRangeUtils;
// import com.example.auction.domain.searchLog.service.SearchLogService;
// import com.github.benmanes.caffeine.cache.Cache;
//
// import lombok.RequiredArgsConstructor;
//
// @Service
// @RequiredArgsConstructor
// public class SearchCacheService {
//
// 	private final SearchLogService searchLogService;
//
// 	@Qualifier("searchLogCache")
// 	private final Cache<String, Long> searchLogCache;
//
// 	@Qualifier("topKeywordCache")
// 	private final Cache<String, List<String>> topKeywordCache;
//
//
// 	//프로덕트에서 검색 시 검색기록 저장
// 	public void saveSearchLog(String keyword) {
// 		String key = keyword + ":" + UUID.randomUUID();
// 		searchLogCache.put(key, System.currentTimeMillis());
// 	}
//
// 	//사용자 요청시간과 겹치는 검색 기록들의 집계 결과 가져옴
// 	public Map<String, Long> getSearchLogsTimeRange(long startMillis, long endMillis) {
// 		return searchLogCache.asMap().entrySet().stream()
// 			.filter(entry -> {
// 				Long time = entry.getValue();
// 				return time >= startMillis && time < endMillis;
// 			})
// 			.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
// 	}
//
// 	public List<String> saveTopKeywords() {
//
// 		long now = System.currentTimeMillis();
// 		TimeRange timeRange = TimeRangeUtils.getPreviousTimeBlock(now);
// 		String cacheKey = "top10:" + getCurrentBlockKey(now);
//
// 		//없으면 집계하고 캐시에 저장
//
// 		//사용자 요청 시간 기준 집계 결과 가져옴
// 		Map<String, Long> searchLogs = getSearchLogsTimeRange(timeRange.start, timeRange.end);
//
// 		//시간 범위 내 검색 기록들을 가져옴
// 		Map<String, Long> timeRangeList = searchLogs.keySet().stream()
// 			.map(k -> k.split(":")[0])
// 			.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
//
// 		//위에서 가져온 검색 기록을 바탕으로 인기 검색어를 집계함
// 		List<String> topKeywords = timeRangeList.entrySet().stream()
// 			.sorted(Map.Entry.<String, Long>comparingByValue().reversed())
// 			.limit(10)
// 			.map(Map.Entry::getKey)
// 			.toList();
//
// 		//인기 검색어 등록
// 		topKeywordCache.put(cacheKey, topKeywords);
//
// 		return topKeywords;
// 	}
//
// 	//인기 검색어 조회
// 	public List<String> getTopKeywords() {
//
// 		long now = System.currentTimeMillis();
// 		// TimeRange timeRange = TimeRangeUtils.getPreviousTimeBlock(now);
// 		String cacheKey = "top10:" + getCurrentBlockKey(now);
//
// 		List<String> keywords = topKeywordCache.getIfPresent(cacheKey);
//
// 		if (keywords != null) {
// 			return keywords;
// 		}
//
// 		return saveTopKeywords();
// 	}
//
// 	//----------------
//
// 	// public PageResponseDto getSearchCache(String cacheKey) {
// 	// 	return searchCache.getIfPresent(cacheKey);
// 	// }
// 	//
// 	// public void saveSearchCache(String cacheKey, PageResponseDto dto) {
// 	// 	searchCache.put(cacheKey, dto);
// 	// }
//
// }
