package com.example.auction.domain.auctionbid.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import com.example.auction.common.exception.BaseCode;

@Getter
@RequiredArgsConstructor
public enum AuctionBidErrorCode implements BaseCode {

	NOT_FOUND_Bid(HttpStatus.CONFLICT, "D001", "요청하신 로그를 찾을 수 없습니다."),
	BID_PRICE_BELOW_START(HttpStatus.BAD_REQUEST, "B001", "입찰 금액은 시작가보다 높아야 합니다."),
	BID_PRICE_BELOW_HIGHEST(HttpStatus.BAD_REQUEST, "B002", "입찰 금액은 현재 최고가보다 높아야 합니다."),
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

}
