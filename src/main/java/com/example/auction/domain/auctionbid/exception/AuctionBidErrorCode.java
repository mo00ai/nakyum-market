package com.example.auction.domain.auctionbid.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import com.example.auction.common.exception.BaseCode;

@Getter
@RequiredArgsConstructor
public enum AuctionBidErrorCode implements BaseCode {

	NOT_FOUND_BID(HttpStatus.CONFLICT, "B001", "요청하신 로그를 찾을 수 없습니다."),
	BID_PRICE_BELOW_START(HttpStatus.BAD_REQUEST, "B002", "입찰 금액은 시작가보다 높아야 합니다."),
	BID_PRICE_BELOW_HIGHEST(HttpStatus.BAD_REQUEST, "B003", "입찰 금액은 현재 최고가보다 높아야 합니다."),
	REDIS_LOGS_NOT_FOUND(HttpStatus.NOT_FOUND, "B004", "입찰내역이 없는 경매입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

}
