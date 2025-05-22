package com.example.auction.domain.dips.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.example.auction.domain.dips.entity.Dips;

@Getter
@RequiredArgsConstructor
public class DipsFindResponseDto {

	private final Long productId;

	private final String productName;

	private final Long price;

	public static DipsFindResponseDto toDto(Dips dips) {
		return new DipsFindResponseDto(
			dips.getProduct().getId(),
			dips.getProduct().getName(),
			dips.getProduct().getFinalPrice()
		);
	}

}
