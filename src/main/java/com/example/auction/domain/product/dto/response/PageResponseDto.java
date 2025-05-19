package com.example.auction.domain.product.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder
@RequiredArgsConstructor
public class PageResponseDto {

	private final List<ProductResponseDto> productList; //게시글 목록

	private final int nowPage; //현재 페이지

	private final int pageSize; // 한 페이지 당 몇 개의 게시글인지

	private final int totalPages; //전체 페이지

	private final boolean hasNext; // 화살표

	private final boolean hasPrevious; //이전 화살표

	private final int startPage; // 페지징 범위 (현재 페이지 기준 -2)

	private final int endPage; //페이징 범위 (현재 페이지 기준 +2)

	public static PageResponseDto from(List<ProductResponseDto> productList, int nowPage, int pageSize, int totalPages,
		boolean hasNext, boolean hasPrevious, int startPage, int endPage) {

		return PageResponseDto.builder()
			.productList(productList)
			.nowPage(nowPage)
			.pageSize(pageSize)
			.totalPages(totalPages)
			.hasNext(hasNext)
			.hasPrevious(hasPrevious)
			.startPage(startPage)
			.endPage(endPage)
			.build();
	}

}
