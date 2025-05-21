package com.example.auction.domain.product.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auction.common.response.CommonResponse;
import com.example.auction.domain.user.entity.UserRole;
import com.example.auction.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

	private final JwtUtil jwtUtil;

	@GetMapping
	public CommonResponse<String> getToken() {
		String token = jwtUtil.createToken(1L, "k", "k", UserRole.ADMIN);
		return CommonResponse.ok(token);
	}
}
