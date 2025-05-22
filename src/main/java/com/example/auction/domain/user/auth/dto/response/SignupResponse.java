package com.example.auction.domain.user.auth.dto.response;

import lombok.Getter;

@Getter
public class SignupResponse {

    private final String userName;

    public SignupResponse(String userName) {
        this.userName = userName;
    }
}
