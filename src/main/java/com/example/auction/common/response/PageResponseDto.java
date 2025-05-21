package com.example.auction.common.response;

import java.util.List;

import org.springframework.data.domain.Page;

import com.example.auction.domain.product.dto.response.ProductResponseDto;

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

	public static PageResponseDto from(Page<ProductResponseDto> allPage) {

		int nowPage = allPage.getNumber() + 1;
		int pageRange = 5;
		int totalPages = allPage.getTotalPages();
		int startPage = ((nowPage - 1) / pageRange) * pageRange + 1;
		int endPage = Math.min(startPage + pageRange - 1, totalPages);
		boolean hasPrevious = startPage > 1;
		boolean hasNext = endPage < totalPages;

		return PageResponseDto.builder()
			.productList(allPage.getContent())
			.nowPage(nowPage)
			.pageSize(allPage.getSize())
			.totalPages(totalPages)
			.hasNext(hasNext)
			.hasPrevious(hasPrevious)
			.startPage(startPage)
			.endPage(endPage)
			.build();
	}

}
