package com.example.auction.domain.product.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import com.example.auction.common.exception.BaseCode;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements BaseCode {

	PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "요청하신 상품을 찾을 수 없습니다."),

	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

}
