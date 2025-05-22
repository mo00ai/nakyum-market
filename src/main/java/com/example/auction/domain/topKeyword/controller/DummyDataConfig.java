package com.example.auction.domain.topKeyword.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.auction.common.service.RedisCacheService;
import com.example.auction.domain.searchLog.entity.SearchLog;
import com.example.auction.domain.searchLog.repository.SearchLogRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class DummyDataConfig {

	// private final SearchCacheService cacheService;
	private final RedisCacheService redisCacheService;

	@Bean
	public CommandLineRunner initSearchLog(
		SearchLogRepository searchLogRepository,
		RedisCacheService redisCacheService // searchLogCache 포함한 서비스
	) {
		return args -> {
			List<String> keywords = Arrays.asList(
				"아이폰", "맥북", "자전거", "의자", "책상", "조명", "모니터", "키보드", "마우스", "카메라"
			);
			Random random = new Random();

			int total = 50000;
			int batchSize = 500;

			List<SearchLog> batch = new ArrayList<>();
			List<String> keywordsForCache = new ArrayList<>();

			for (int i = 0; i < total; i++) {
				String keyword = keywords.get(random.nextInt(keywords.size()));
				batch.add(SearchLog.of(keyword));
				keywordsForCache.add(keyword);

				// 배치 저장
				if (batch.size() == batchSize) {
					searchLogRepository.saveAll(batch);
					saveSearchLogBatch(keywordsForCache);
					batch.clear();
					keywordsForCache.clear();
				}
			}

			// 남은 데이터 처리
			if (!batch.isEmpty()) {
				searchLogRepository.saveAll(batch);
				saveSearchLogBatch(keywordsForCache);
			}
		};
	}

	public void saveSearchLogBatch(List<String> keywords) {
		for (String keyword : keywords) {
			redisCacheService.saveSearchLog(keyword);
		}
	}

}
