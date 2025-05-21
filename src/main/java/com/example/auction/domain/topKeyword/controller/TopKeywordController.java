package com.example.auction.domain.topKeyword.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auction.common.response.CommonResponse;
import com.example.auction.domain.auth.security.CustomUserDetails;
import com.example.auction.domain.topKeyword.dto.response.TopKeywordResponseDto;
import com.example.auction.domain.topKeyword.service.TopKeywordService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/popular-keywords")
@RequiredArgsConstructor
public class TopKeywordController {

	private final TopKeywordService topKeywordService;

	@GetMapping("/v1")
	public CommonResponse<List<TopKeywordResponseDto>> findTopKeywords(
		@AuthenticationPrincipal CustomUserDetails userDetail) {

		return CommonResponse.ok(topKeywordService.findTopKeywords());
	}

	@GetMapping("/v2")
	public CommonResponse<List<TopKeywordResponseDto>> findTopKeywordsV2(
		@AuthenticationPrincipal CustomUserDetails userDetail) {

		return CommonResponse.ok(topKeywordService.findTopKeywordsV2());
	}

}
