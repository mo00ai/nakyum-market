package com.example.auction.domain.wonitem.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.redis.core.RedisHash;

@RedisHash("wonitem")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WonItem {

	@Id
	private String key;

	private Long userId;
	private Long productId;
	private long wonItemPrice;
	private LocalDateTime createdAt;

	public static WonItem of(String key, Long userId, Long productId, long wonItemPrice) {
		return WonItem.builder()
			.key(key)
			.userId(userId)
			.productId(productId)
			.wonItemPrice(wonItemPrice)
			.createdAt(LocalDateTime.now())
			.build();
	}
}
