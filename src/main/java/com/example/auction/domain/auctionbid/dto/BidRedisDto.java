package com.example.auction.domain.auctionbid.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import com.example.auction.domain.auctionbid.dto.request.BidRequestDto;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BidRedisDto {

	private Long productId;

	private Long userId;

	private Long bidPrice;

	private LocalDateTime bidTimeAt;

	public static BidRedisDto of(BidRequestDto bidRequestDto, Long productId, Long userId, LocalDateTime bidTimeAt) {
		return BidRedisDto.builder()
			.productId(productId)
			.userId(userId)
			.bidPrice(bidRequestDto.getBidPrice())
			.bidTimeAt(bidTimeAt)
			.build();
	}
}
