package com.example.auction.domain.topKeyword.dto.response;

import com.example.auction.domain.topKeyword.entity.TopKeyword;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TopKeywordResponseDto {

	private String keyword;

	public static TopKeywordResponseDto from(TopKeyword topKeyword) {
		return TopKeywordResponseDto.builder().keyword(topKeyword.getKeyword()).build();
	}

	public static TopKeywordResponseDto from(String keyword) {
		return TopKeywordResponseDto.builder().keyword(keyword).build();
	}

}
