package com.example.auction.domain.wonitem.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Id;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import org.springframework.data.redis.core.RedisHash;

import com.example.auction.domain.product.entity.Product;
import com.example.auction.domain.user.entity.User;

@RedisHash("wonitem")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WonItem {

	@Id
	private String key;

	private Long productId;
	private String wonItemName;
	private long wonItemPrice;
	private String wonItemImageUrl;

	private Long userId;
	private String nickname;

	private LocalDateTime createdAt;

	public static WonItem of(String key, Product product, User user) {
		return WonItem.builder()
			.key(key)
			.productId(product.getId())
			.wonItemName(product.getName())
			.wonItemPrice(product.getFinalPrice())
			.wonItemImageUrl(product.getImage().getUploadFileName())
			.userId(user.getId())
			.nickname(user.getNickname())
			.createdAt(LocalDateTime.now())
			.build();
	}
}
