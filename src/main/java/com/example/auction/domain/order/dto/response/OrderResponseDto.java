package com.example.auction.domain.order.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import com.example.auction.domain.order.entity.Order;
import com.example.auction.domain.product.entity.Product;
import com.example.auction.domain.user.entity.User;

@Builder
@Getter
@AllArgsConstructor
public class OrderResponseDto {

	private final Long orderId;
	private final Long userId;
	private final String productName;
	private final Long totalPrice;
	private final LocalDateTime createdAt;

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
