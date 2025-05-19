package com.example.auction.domain.user.auth.exception;

import com.example.auction.common.exception.BaseCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseCode {

	INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "A001", "비밀번호가 일치하지 않습니다."),
	UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "A002", "인증되지 않은 요청입니다."),
	TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "A003", "토큰이 만료되었습니다."),
	TOKEN_INVALID(HttpStatus.UNAUTHORIZED, "A004", "유효하지 않은 토큰입니다."),
	NON_PROVIDER(HttpStatus.NOT_FOUND,"A005","지원하지 않는 소셜 로그인입니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
