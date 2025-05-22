package com.example.auction.domain.auctionbid.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auction.common.response.CommonResponse;
import com.example.auction.domain.auctionbid.dto.request.BidRequestDto;
import com.example.auction.domain.auctionbid.service.AuctionBidService;
import com.example.auction.domain.user.auth.security.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuctionBidController {

	private final AuctionBidService auctionbidService;

	@Validated
	@PostMapping("/product/{productId}/auction-bid")
	public CommonResponse<Void> bidSave(
		@PathVariable @NotNull Long productId,
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@Valid @RequestBody BidRequestDto requestDto
	) {
		auctionbidService.bidSave(productId, userDetails.getUser().getId(), requestDto);
		return CommonResponse.created(null);
	}
}
