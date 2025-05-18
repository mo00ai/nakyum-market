package com.example.auction.domain.user.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.http.HttpStatus;
import com.example.auction.common.exception.BaseCode;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseCode {

	//User
	NOT_FOUND_USER(HttpStatus.NOT_FOUND, "U001", "유저를 찾을 수 없습니다."),
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
