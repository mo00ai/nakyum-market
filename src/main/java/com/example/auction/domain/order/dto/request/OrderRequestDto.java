package com.example.auction.domain.order.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderRequestDto {

	@NotNull
	private final Long productId;

	@NotNull
	private final Long totalPrice;
}
