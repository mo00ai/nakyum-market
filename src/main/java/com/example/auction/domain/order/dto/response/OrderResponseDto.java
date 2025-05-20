package com.example.auction.domain.order.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

import com.example.auction.domain.order.entity.Order;
import com.example.auction.domain.product.entity.Product;
import com.example.auction.domain.user.entity.User;

@Builder
public record OrderResponseDto(
	Long orderId,
	Long userId,
	String productName,
	Long totalPrice,
	LocalDateTime createdAt
) {

	public static OrderResponseDto from(Order order, User user, Product product) {
		return OrderResponseDto.builder()
			.orderId(order.getId())
			.userId(user.getId())
			.productName(product.getName())
			.totalPrice(order.getTotalPrice())
			.createdAt(order.getCreateAt())
			.build();
	}
}
