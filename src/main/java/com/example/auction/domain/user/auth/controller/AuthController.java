package com.example.auction.domain.user.auth.controller;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.auction.domain.user.auth.security.CustomUserDetails;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

	//    private final AuthService authService;
	//
	//    @PostMapping("/auth/signup")
	//    public SignupResponse signup(@Valid @RequestBody SignupRequest signupRequest) {
	//        return authService.signup(signupRequest);
	//    }
	//
	//    @PostMapping("/auth/signin")
	//    public SigninResponse signin(@Valid @RequestBody SigninRequest signinRequest) {
	//        return authService.signin(signinRequest);
	//    }

	@PostMapping("/logout")
	public void logout(@AuthenticationPrincipal CustomUserDetails userDetail) {

	}
}
