package com.example.auction.domain.user.auth.dto.response;

import lombok.Getter;

@Getter
public class SigninResponse {

	private final String userName;

	public SigninResponse(String userName) {
		this.userName = userName;
	}
}
