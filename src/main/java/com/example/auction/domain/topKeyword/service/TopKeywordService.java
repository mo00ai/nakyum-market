package com.example.auction.domain.topKeyword.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.auction.common.service.RedisCacheService;
import com.example.auction.domain.topKeyword.dto.response.TopKeywordResponseDto;
import com.example.auction.domain.topKeyword.entity.TopKeyword;
import com.example.auction.domain.topKeyword.repository.TopKeywordRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TopKeywordService {

	private final TopKeywordRepository topKeywordRepository;
	// private final SearchCacheService searchCacheService;
	private final RedisCacheService redisCacheService;

	@Transactional
	public void deleteAll() {

		topKeywordRepository.deleteAll();
	}

	@Transactional
	public void saveTopKeyword(List<TopKeyword> keywords) {

		topKeywordRepository.saveAll(keywords);
	}

	@Transactional(readOnly = true)
	public List<TopKeywordResponseDto> findTopKeywords() {

		List<TopKeywordResponseDto> dtoList = topKeywordRepository.findAll().stream()
			.map(TopKeywordResponseDto::from)
			.collect(Collectors.toList());

		return dtoList;
	}

	public List<TopKeywordResponseDto> findTopKeywordsV2() {

		List<String> topKeywords = redisCacheService.getTopKeywords();

		List<TopKeywordResponseDto> dtoList = topKeywords.stream()
			.map(keyword -> TopKeywordResponseDto.from(keyword))
			.collect(Collectors.toList());

		return dtoList;
	}

}
