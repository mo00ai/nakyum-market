package com.example.auction.domain.product.dto.response;

import com.example.auction.domain.product.entity.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductWithdrawResponseDto {

	private Long id;

	private String name;

	private String message;

	public static ProductWithdrawResponseDto from(Product product, String message) {
		return ProductWithdrawResponseDto.builder()
			.id(product.getId())
			.name(product.getName())
			.message(message)
			.build();
	}
}
