package com.example.auction.domain.topKeyword.service;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.auction.domain.topKeyword.dto.response.TopKeywordResponseDto;
import com.example.auction.domain.topKeyword.entity.TopKeyword;
import com.example.auction.domain.topKeyword.repository.TopKeywordRepository;

@Service
@RequiredArgsConstructor
public class TopKeywordService {

	private final TopKeywordRepository topKeywordRepository;

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

}
