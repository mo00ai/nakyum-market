package com.example.auction.domain.TopKeyword.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.auction.common.response.CommonResponse;
import com.example.auction.domain.TopKeyword.dto.response.TopKeywordResponseDto;
import com.example.auction.domain.TopKeyword.service.TopKeywordService;
import com.example.auction.domain.auth.security.CustomUserDetails;

import lombok.RequiredArgsConstructor;

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
