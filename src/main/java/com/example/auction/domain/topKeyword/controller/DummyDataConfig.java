package com.example.auction.domain.topKeyword.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.boot.CommandLineRunner;

import com.example.auction.common.service.RedisCacheService;
import com.example.auction.domain.searchLog.entity.SearchLog;
import com.example.auction.domain.searchLog.repository.SearchLogRepository;

import lombok.RequiredArgsConstructor;

// @Configuration
@RequiredArgsConstructor
public class DummyDataConfig {

	// private final SearchCacheService cacheService;
	private final RedisCacheService redisCacheService;

	private static final int TOTAL = 100000;
	private static final int BATCH_SIZE = 1000;
	private static final int THREAD_COUNT = 10;

	// @Bean
	public CommandLineRunner initSearchLog(SearchLogRepository searchLogRepository) {
		return args -> {
			List<String> keywords = Arrays.asList("아이폰", "맥북", "자전거", "의자", "책상", "조명", "모니터", "키보드", "마우스", "카메라");
			ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
			List<Future<?>> futures = new ArrayList<>();

			for (int i = 0; i < TOTAL; i += BATCH_SIZE) {
				futures.add(executor.submit(() -> {
					Random random = new Random();
					List<SearchLog> batch = new ArrayList<>();
					List<String> keywordsForCache = new ArrayList<>();

					for (int j = 0; j < BATCH_SIZE; j++) {
						String keyword = keywords.get(random.nextInt(keywords.size()));
						batch.add(SearchLog.of(keyword));
						keywordsForCache.add(keyword);
					}

					searchLogRepository.saveAll(batch);
					saveSearchLogBatch(keywordsForCache);
				}));
			}

			// 모든 작업 완료 대기
			for (Future<?> future : futures) {
				future.get(); // 예외 발생 시 propagate
			}

			executor.shutdown();
		};
	}

	public void saveSearchLogBatch(List<String> keywords) {
		for (String keyword : keywords) {
			redisCacheService.saveSearchLog(keyword);
		}
	}

}
