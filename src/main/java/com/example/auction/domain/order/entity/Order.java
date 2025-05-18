package com.example.auction.domain.order.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "orders")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private Long userId;

	private int totalPrice;

	private OrderStatus orderStatus;

	// 내부에서만 사용하는 생성자
	@Builder
	private Order(Long userId, int totalPrice, OrderStatus orderStatus) {
		this.userId = userId;
		this.totalPrice = totalPrice;
		this.orderStatus = orderStatus;

	}

	/**
	 * 정적
	 *
	 */
	public static Order of(Long userId, int totalPrice) {
		return Order.builder()
			.userId(userId)
			.totalPrice(totalPrice)
			.orderStatus(OrderStatus.NON_ORDER)
			.build();
	}

}
