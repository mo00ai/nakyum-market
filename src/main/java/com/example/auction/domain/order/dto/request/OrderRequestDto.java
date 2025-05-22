package com.example.auction.domain.order.dto.request;

import java.util.List;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderRequestDto {

	@NotNull
	private final List<Long> productIds;

}
