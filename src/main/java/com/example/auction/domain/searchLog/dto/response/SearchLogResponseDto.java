package com.example.auction.domain.searchLog.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SearchLogResponseDto {

	private String keyword;

}
