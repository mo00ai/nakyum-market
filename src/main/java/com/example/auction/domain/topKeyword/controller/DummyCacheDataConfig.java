package com.example.auction.domain.topKeyword.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.auction.common.service.RedisCacheService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DummyCacheDataConfig {

	private final RedisCacheService redisCacheService;
	private static final int TOTAL = 10000;
	private static final int BATCH_SIZE = 500;
	private static final int THREAD_COUNT = 10;

	@Bean
	public CommandLineRunner initCacheOnlySearchLogs() {
		return args -> {
			List<String> keywords = Arrays.asList("아이폰", "맥북", "자전거", "의자", "책상", "조명", "모니터", "키보드", "마우스", "카메라");
			ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
			List<Future<?>> futures = new java.util.ArrayList<>();

			long now = System.currentTimeMillis();
			long startMillis = now - 20 * 60 * 1000L;
			long endMillis = now + 30 * 60 * 1000L;

			for (int i = 0; i < TOTAL; i += BATCH_SIZE) {
				futures.add(executor.submit(() -> {
					Random random = new Random();
					for (int j = 0; j < BATCH_SIZE; j++) {
						String keyword = keywords.get(random.nextInt(keywords.size()));
						long randomTime = startMillis + (long)(random.nextDouble() * (endMillis - startMillis));

						redisCacheService.saveSearchLog(keyword, randomTime);
					}
				}));
			}

			for (Future<?> future : futures) {
				future.get();
			}

			executor.shutdown();
		};
	}
}
