package com.example.auction.domain.order.controller;

import java.util.List;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auction.common.response.CommonResponse;
import com.example.auction.domain.order.dto.request.OrderRequestDto;
import com.example.auction.domain.order.dto.response.OrderResponseDto;
import com.example.auction.domain.order.service.OrderService;
import com.example.auction.domain.user.auth.security.CustomUserDetails;
import com.example.auction.domain.user.entity.User;

@RequestMapping("/api/orders")
@RestController
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@PreAuthorize("hasRole('USER')")
	@PostMapping
	public CommonResponse<OrderResponseDto> orderSave(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Valid @RequestBody OrderRequestDto requestDto) {

		User loginUser = userDetails.getUser();
		List<Long> productIds = requestDto.getProductIds();

		OrderResponseDto responseDto = orderService.saveOrder(loginUser, productIds);

		return CommonResponse.created(responseDto);
	}

}
