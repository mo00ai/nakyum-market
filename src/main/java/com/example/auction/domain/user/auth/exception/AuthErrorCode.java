package com.example.auction.domain.user.auth.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.http.HttpStatus;

import com.example.auction.common.exception.BaseCode;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements BaseCode {

	INVALID_LOGIN(HttpStatus.UNAUTHORIZED, "A001", "이메일 또는 비밀번호가 일치하지 않습니다."),
	SOCIAL_ONLY(HttpStatus.BAD_REQUEST, "A002", "이미 소셜 로그인한 상태에서는 접근할 수 없습니다"),
	ALREADY_LOGGED_IN(HttpStatus.UNAUTHORIZED, "A003", "이미 로그인된 상태입니다. 로그아웃 후 이용해주세요."),
	ALREAD_REGISTERD(HttpStatus.BAD_REQUEST, "A004", "이미 등록된 이메일입니다."),
	NON_PROVIDER(HttpStatus.NOT_FOUND, "A005", "지원하지 않는 소셜 로그인입니다."),
	INVALID_EMAIL_VERIFICATION(HttpStatus.BAD_REQUEST, "A006", "코드가 일치하지 않습니다."),
	EMAIL_NOT_VERIFIED(HttpStatus.BAD_REQUEST, "A007", "확인되지 않은 이메일입니다."),
	LOGIN_LOCKED(HttpStatus.TOO_MANY_REQUESTS, "A008", "로그인 시도 횟수를 초과했습니다. 5분 후 다시 시도해주세요."),
	AUTH_CODE_ALREADY_SENT(HttpStatus.TOO_MANY_REQUESTS, "A009", "인증번호가 이미 전송되었습니다."),
	AUTH_CODE_ATTEMPT_EXCEEDED(HttpStatus.FORBIDDEN, "A010", "인증번호 시도 횟수 초과하였습니다."),
	NOT_AUTHENTICATED(HttpStatus.NOT_FOUND, "A011", "로그인 상태가 아닙니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
