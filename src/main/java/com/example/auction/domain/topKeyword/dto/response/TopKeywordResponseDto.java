package com.example.auction.domain.topKeyword.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import com.example.auction.domain.topKeyword.entity.TopKeyword;

@Getter
@Builder
@AllArgsConstructor
public class TopKeywordResponseDto {

	private String keyword;

	public static TopKeywordResponseDto from(TopKeyword topKeyword) {
		return TopKeywordResponseDto.builder().keyword(topKeyword.getKeyword()).build();
	}

}
