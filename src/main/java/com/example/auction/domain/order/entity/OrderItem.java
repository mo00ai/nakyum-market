package com.example.auction.domain.order.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// 어떤 주문에 속하는지
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "order_id", nullable = false)
	private Order order;

	// 어떤 상품인지
	@Column(nullable = false)
	private Long productId;

	// 수량
	@Column(nullable = false)
	private int quantity = 1;

	// 단가
	@Column(nullable = false)
	private int price;

	@Builder
	private OrderItem(Order order, Long productId, int quantity, int price) {
		this.order = order;
		this.productId = productId;
		this.quantity = quantity;
		this.price = price;
	}

	/**
	 * 정적 팩토리 메서드로 생성 (기본 수량 1)
	 */
	public static OrderItem of(Order order, Long productId, int price) {
		return OrderItem.builder()
			.order(order)
			.productId(productId)
			.quantity(1)
			.price(price)
			.build();
	}

	/**
	 * 금액 계산
	 */
	public int getTotalPrice() {
		return this.price * this.quantity;
	}
}
