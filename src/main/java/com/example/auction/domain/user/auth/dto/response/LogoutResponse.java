package com.example.auction.domain.user.auth.dto.response;

import java.time.LocalDateTime;

import lombok.Getter;

@Getter
public class LogoutResponse {
	private final int status;
	private final String message;
	private final LocalDateTime timestamp;
	private final String path;

	public LogoutResponse(int status, String message, String path) {
		this.status = status;
		this.message = message;
		this.timestamp = LocalDateTime.now();
		this.path = path;
	}
}
