package com.example.auction.common.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;

<<<<<<<< Updated upstream:src/main/java/com/example/auction/common/response/PageResponse.java
========
import com.example.auction.domain.product.dto.response.ProductResponseDto;

>>>>>>>> Stashed changes:src/main/java/com/example/auction/common/response/PageResponseDto.java
@Getter
@Builder
@RequiredArgsConstructor
public class PageResponse<T> {

	private final List<T> content; //게시글 목록

	private final int nowPage; //현재 페이지

	private final int pageSize; // 한 페이지 당 몇 개의 게시글인지

	private final int totalPages; //전체 페이지

	private final boolean hasNext; // 화살표

	private final boolean hasPrevious; //이전 화살표

	private final int startPage; // 페지징 범위 (현재 페이지 기준 -2)

	private final int endPage; //페이징 범위 (현재 페이지 기준 +2)

	public static <T> PageResponse<T> from(Page<T> allPage) {

		int nowPage = allPage.getNumber() + 1;
		int pageRange = 5;
		int totalPages = allPage.getTotalPages();
		int startPage = ((nowPage - 1) / pageRange) * pageRange + 1;
		int endPage = Math.min(startPage + pageRange - 1, totalPages);
		boolean hasPrevious = startPage > 1;
		boolean hasNext = endPage < totalPages;

		return PageResponse.<T>builder()
			.content(allPage.getContent())
			.nowPage(nowPage)
			.pageSize(allPage.getSize())
			.totalPages(totalPages)
			.hasNext(hasNext)
			.hasPrevious(hasPrevious)
			.startPage(startPage)
			.endPage(endPage)
			.build();
	}

<<<<<<<< Updated upstream:src/main/java/com/example/auction/common/response/PageResponse.java
	public static <T> PageResponse<T> fromRedis(List<T> allContent, int page, int size) {
		if (page < 1) {
			page = 1;
		}

		int totalElements = allContent.size();
		int totalPages = (allContent.size() + size - 1) / size;

		int fromIndex = (page - 1) * size;
		int toIndex = Math.min(fromIndex + size, totalElements);
		List<T> content = fromIndex >= totalElements ? List.of() : allContent.subList(fromIndex, toIndex);

		int pageRange = 5;
		int startPage = ((page - 1) / pageRange) * pageRange + 1;
		int endPage = Math.min(startPage + pageRange - 1, totalPages);

		return PageResponse.<T>builder()
			.content(content)
			.nowPage(page)
			.pageSize(size)
			.totalPages(totalPages)
			.hasPrevious(startPage > 1)
			.hasNext(endPage < totalPages)
========
	public static PageResponseDto fromRedis(List<ProductResponseDto> content, int page, int size, int totalElements) {
		int totalPages = (int)Math.ceil((double)totalElements / size);
		int nowPage = page;
		int pageRange = 5;
		int startPage = ((nowPage - 1) / pageRange) * pageRange + 1;
		int endPage = Math.min(startPage + pageRange - 1, totalPages);
		boolean hasPrevious = startPage > 1;
		boolean hasNext = endPage < totalPages;

		return PageResponseDto.builder()
			.productList(content)
			.nowPage(nowPage)
			.pageSize(size)
			.totalPages(totalPages)
			.hasNext(hasNext)
			.hasPrevious(hasPrevious)
>>>>>>>> Stashed changes:src/main/java/com/example/auction/common/response/PageResponseDto.java
			.startPage(startPage)
			.endPage(endPage)
			.build();
	}
}
