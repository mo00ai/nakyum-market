package com.example.auction.domain.dips.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;

import com.example.auction.common.exception.BaseCode;

@Getter
@RequiredArgsConstructor
public enum DipsErrorCode implements BaseCode {

	NOT_FOUND_DIPS(HttpStatus.CONFLICT, "D001", "요청하신 찜 데이터를 찾을 수 없습니다."),
	;

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;

}
