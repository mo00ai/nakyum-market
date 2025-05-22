package com.example.auction.domain.topKeyword.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auction.common.response.CommonResponse;
import com.example.auction.domain.topKeyword.dto.response.TopKeywordResponseDto;
import com.example.auction.domain.topKeyword.service.TopKeywordService;
import com.example.auction.domain.user.auth.security.CustomUserDetails;

@RestController
@RequestMapping("/api/popular-keywords")
@RequiredArgsConstructor
public class TopKeywordController {

	private final TopKeywordService topKeywordService;

	@GetMapping
	public CommonResponse<List<TopKeywordResponseDto>> findTopKeywords(
		@AuthenticationPrincipal CustomUserDetails userDetail) {

		return CommonResponse.ok(topKeywordService.findTopKeywords());
	}

}
