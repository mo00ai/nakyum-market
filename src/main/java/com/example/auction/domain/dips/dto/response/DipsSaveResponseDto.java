package com.example.auction.domain.dips.dto.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.example.auction.domain.dips.entity.Dips;

@Getter
@RequiredArgsConstructor
public class DipsSaveResponseDto {

	private final Long userId;

	private final Long productId;

	public static DipsSaveResponseDto toDto(Dips dips) {
		return new DipsSaveResponseDto(dips.getUser().getId(), dips.getProduct().getId());
	}

}
