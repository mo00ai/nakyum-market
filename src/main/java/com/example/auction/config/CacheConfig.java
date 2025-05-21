package com.example.auction.config;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

@Configuration
@EnableCaching
public class CacheConfig {

	@Bean(name = "searchLogCache")
	public Cache<String, Long> searchLogCache() {
		return Caffeine.newBuilder()
			.expireAfterWrite(20, TimeUnit.MINUTES)
			.maximumSize(10000)
			.build();
	}

	@Bean(name = "topKeywordCache")
	public Cache<String, List<String>> topKeywordCache() {
		return Caffeine.newBuilder()
			.expireAfterWrite(10, TimeUnit.MINUTES)
			.maximumSize(100)
			.build();
	}

	// @Bean(name = "searchCache")
	// public Cache<String, PageResponseDto> searchCache() {
	// 	return Caffeine.newBuilder()
	// 		.expireAfterWrite(10, TimeUnit.MINUTES)
	// 		.maximumSize(1000)
	// 		.build();
	// }

}
