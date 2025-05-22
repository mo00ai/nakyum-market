package com.example.auction.domain.wonitem.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import com.example.auction.common.exception.BaseCode;

@Getter
@RequiredArgsConstructor
public enum WonItemErrorCode implements BaseCode {

	UNAUTHORIZED_WON_ITEM(HttpStatus.FORBIDDEN, "O001", "낙찰 이력이 존재하지 않은 상품이 포함되어 있습니다."),
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

}
