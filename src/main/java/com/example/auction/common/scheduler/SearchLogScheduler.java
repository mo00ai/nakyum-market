package com.example.auction.common.scheduler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.auction.domain.searchLog.dto.response.SearchLogResponseDto;
import com.example.auction.domain.searchLog.entity.SearchLog;
import com.example.auction.domain.searchLog.service.SearchLogService;
import com.example.auction.domain.topKeyword.entity.TopKeyword;
import com.example.auction.domain.topKeyword.service.TopKeywordService;

@Component
@RequiredArgsConstructor
public class SearchLogScheduler {

	private final SearchLogService searchLogService;
	private final TopKeywordService topKeywordService;

	@Scheduled(cron = "0 */10 * * * *")
	public void updateTopKeyword() {

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime minusRange = now.minusMinutes(10);

		List<SearchLog> searchLogs = searchLogService.findKeywords(minusRange);
		searchLogService.deleteSearchLogs(searchLogs);

		List<SearchLogResponseDto> TopKeywords = searchLogService.findPopularKeywords();

		List<TopKeyword> topKeywords = TopKeywords.stream()
			.map(dto -> TopKeyword.of(dto.getKeyword()))
			.collect(Collectors.toList());

		topKeywordService.deleteAll();
		topKeywordService.saveTopKeyword(topKeywords);

	}
}
