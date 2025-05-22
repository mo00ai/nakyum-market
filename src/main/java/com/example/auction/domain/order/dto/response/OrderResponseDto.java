package com.example.auction.domain.order.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import com.example.auction.domain.order.entity.Order;
import com.example.auction.domain.order.entity.OrderItem;
import com.example.auction.domain.user.entity.User;

@Builder
@Getter
@AllArgsConstructor
public class OrderResponseDto {

	private final Long orderId;
	private final Long userId;
	private final List<String> orderItemName;
	private final Long totalPrice;
	private final LocalDateTime createdAt;

	public static OrderResponseDto from(Order order, User user, List<OrderItem> orderItems) {
		List<String> orderItemName = orderItems.stream()
			.map(extractOrderItemName())
			.toList();

		return OrderResponseDto.builder()
			.orderId(order.getId())
			.userId(user.getId())
			.orderItemName(orderItemName)
			.totalPrice(order.getTotalPrice())
			.createdAt(order.getCreateAt())
			.build();
	}

	private static Function<OrderItem, String> extractOrderItemName() {
		return orderitem -> orderitem.getProduct().getName();
	}
}
