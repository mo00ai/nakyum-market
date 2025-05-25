package com.example.auction.domain.searchLog.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.auction.domain.searchLog.dto.response.SearchLogResponseDto;
import com.example.auction.domain.searchLog.entity.SearchLog;
import com.example.auction.domain.searchLog.repository.SearchLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SearchLogService {

	private final SearchLogRepository searchLogRepository;

	@Transactional(readOnly = true)
	public List<SearchLogResponseDto> findPopularKeywords() {

		List<SearchLogResponseDto> popularKeywords = searchLogRepository.findPopularKeywords(PageRequest.of(0, 10));

		return popularKeywords;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void saveSearchLog(String keyword) {

		LocalDateTime dateTime = Instant.ofEpochMilli(System.currentTimeMillis())
			.atZone(ZoneId.systemDefault())  // 또는 ZoneId.of("Asia/Seoul")
			.toLocalDateTime();

		searchLogRepository.save(SearchLog.of(keyword, dateTime));

	}

	@Transactional
	public void deleteSearchLogs(List<SearchLog> SearchLogList) {

		searchLogRepository.deleteAll(SearchLogList);

	}

	@Transactional(readOnly = true)
	public List<SearchLog> findKeywords(LocalDateTime minusRange) {

		return searchLogRepository.findKeywords(minusRange);

	}

	@Transactional
	public void saveSearchLogs(List<SearchLog> logs) {

		searchLogRepository.saveAll(logs);
	}

}
