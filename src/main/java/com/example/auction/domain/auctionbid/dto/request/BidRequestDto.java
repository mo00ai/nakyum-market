package com.example.auction.domain.auctionbid.dto.request;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BidRequestDto {

	@NotNull(message = "입찰가는 필수입니다.")
	private Long bidPrice;
}
