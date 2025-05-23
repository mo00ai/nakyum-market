package com.example.auction.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CacheType {

	TOP_KEYWORD("topKeywordCache", 600, 1000),    // 10분
	SEARCH_LOG("searchLogCache", 1200, 10000);    // 20분

	private final String cacheName;
	private final int expiredAfterWrite; // seconds
	private final int maximumSize;

}
