package com.example.auction.domain.product.dto.response;

import java.time.LocalDate;

import com.example.auction.domain.product.entity.Product;
import com.querydsl.core.annotations.QueryProjection;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductResponseDto {

	private Long id;

	private String imgUrl;

	private String name;

	private String description;

	private Long startPrice;

	private Long unitPrice;

	private Long finalPrice;

	private LocalDate startedAt;

	private LocalDate endedAt;

	private int count;

	public static ProductResponseDto from(String imgUrl, Product product) {
		return ProductResponseDto.builder()
			.id(product.getId())
			.imgUrl(imgUrl)
			.name(product.getName())
			.description(product.getDescription())
			.startPrice(product.getStartPrice())
			.unitPrice(product.getUnitPrice())
			.finalPrice(product.getFinalPrice())
			.startedAt(product.getStartedAt())
			.endedAt(product.getEndedAt())
			.count(product.getCount())
			.build();

	}

	@QueryProjection
	public ProductResponseDto(Long id, String imgUrl, String name, String description, Long startPrice, Long unitPrice,
		Long finalPrice, LocalDate startedAt, LocalDate endedAt, int count) {
		this.id = id;
		this.imgUrl = imgUrl;
		this.name = name;
		this.description = description;
		this.startPrice = startPrice;
		this.unitPrice = unitPrice;
		this.finalPrice = finalPrice;
		this.startedAt = startedAt;
		this.endedAt = endedAt;
		this.count = count;
	}
}
