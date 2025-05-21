package com.example.auction.domain.order.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import com.example.auction.common.exception.BaseCode;

@Getter
@RequiredArgsConstructor
public enum OrderErrorCode implements BaseCode {

	ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "주문한 상품이 없습니다."),
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

}
