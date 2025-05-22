package com.example.auction.domain.wonitem.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class WonItemTestRequestDto {

	private final Long productId;
	private final Long userId;
}
