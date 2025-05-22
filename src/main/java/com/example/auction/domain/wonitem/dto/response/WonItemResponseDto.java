package com.example.auction.domain.wonitem.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import com.example.auction.domain.wonitem.entity.WonItem;

@Builder
@Getter
@AllArgsConstructor
public class WonItemResponseDto {

	private final Long productId;
	private final String name;
	private final long finalPrice;
	private final String nickname;
	private final long ttl;

	public static WonItemResponseDto from(WonItem wonItem, long ttl) {
		return WonItemResponseDto.builder()
			.productId(wonItem.getProductId())
			.name(wonItem.getWonItemName())
			.finalPrice(wonItem.getWonItemPrice())
			.nickname(wonItem.getNickname())
			.ttl(ttl)
			.build();
	}
}
