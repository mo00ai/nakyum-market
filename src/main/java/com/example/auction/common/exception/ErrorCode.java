package com.example.auction.common.exception;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode implements BaseCode {

	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "유효하지 않은 입력 값입니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C002", "허용되지 않은 요청 방식입니다."),
	ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "요청한 엔티티를 찾을 수 없습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "내부 서버 오류가 발생했습니다."),
	INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C005", "유효하지 않은 타입의 값입니다."),

	//Dips
	NOT_FOUND_DIPS(HttpStatus.CONFLICT, "L001", "찜 데이터가 존재하지 않습니다"),

	//DB
	DB_LOCK_CONFLICT(HttpStatus.CONFLICT, "L002", "데이터베이스 락 상태로 요청을 처리할 수 없습니다."),

	//JWT
	INVALID_JWT_SIGNATURE(HttpStatus.UNAUTHORIZED, "A001", "유효하지 않은 JWT 서명입니다."),
	EXPIRED_JWT_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "만료된 JWT 토큰입니다."),
	UNSUPPORTED_JWT_TOKEN(HttpStatus.BAD_REQUEST, "A003", "지원되지 않는 JWT 토큰입니다."),
	INVALID_JWT_TOKEN(HttpStatus.BAD_REQUEST, "A004", "잘못된 JWT 토큰입니다."),
	UNKNOWN_JWT_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "A005", "알 수 없는 JWT 관련 오류가 발생했습니다."),
	NOT_FOUND_JWT_TOKEN(HttpStatus.NOT_FOUND, "A006", "존재하지않는 JWT 토큰입니다."),

	//ImageAspect
	INVALID_IMAGE_SIZE(HttpStatus.BAD_REQUEST, "I001", "이미지 파일의 크기는 최대 2MB까지 업로드 가능합니다."),
	INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "I002", "이미지 파일은 .jpg .jpeg .png만 업로드 할 수 있습니다."),
	INVALID_MIME_TYPE(HttpStatus.BAD_REQUEST, "I003", "파일 내용이 이미지가 아닙니다."),

	//File
	FILE_READ_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "F001", "파일을 읽는 중 서버 오류가 발생했습니다.");

	private final HttpStatus httpStatus;
	private final String code;
	private final String message;
}
