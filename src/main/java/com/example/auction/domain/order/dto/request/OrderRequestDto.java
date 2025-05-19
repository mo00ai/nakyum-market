package com.example.auction.domain.order.dto.request;

public record OrderRequestDto(
	Long productId,
	Long totalPrice
) {
}
