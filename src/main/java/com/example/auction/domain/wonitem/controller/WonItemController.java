package com.example.auction.domain.wonitem.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.auction.common.response.CommonResponse;
import com.example.auction.common.response.PageResponse;
import com.example.auction.domain.auth.security.CustomUserDetails;
import com.example.auction.domain.product.entity.Product;
import com.example.auction.domain.product.repository.ProductRepository;
import com.example.auction.domain.user.entity.User;
import com.example.auction.domain.user.repository.UserRepository;
import com.example.auction.domain.wonitem.dto.request.WonItemTestRequestDto;
import com.example.auction.domain.wonitem.dto.response.WonItemResponseDto;
import com.example.auction.domain.wonitem.service.WonItemService;

@RestController
@RequestMapping("/api/won-item")
@RequiredArgsConstructor
public class WonItemController {

	private final WonItemService wonItemService;

	@GetMapping
	public CommonResponse<PageResponse<WonItemResponseDto>> findWonItems(
		@AuthenticationPrincipal CustomUserDetails userDetails,
		@RequestParam(required = false, defaultValue = "1") Integer page,
		@RequestParam(required = false, defaultValue = "5") Integer size) {

		User loginUser = userDetails.getUser();
		PageResponse<WonItemResponseDto> responseDto = wonItemService.findWonItems(loginUser, page, size);
		return CommonResponse.ok(responseDto);
	}

	/**
	 * 테스트용 -> 제일 마지막에 삭제 예정
	 */
	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	@PostMapping("/test")
	public CommonResponse<Void> createWonItem(@RequestBody WonItemTestRequestDto requestDto) {

		List<Product> allProduct = productRepository.findAll();

		User user = userRepository.findById(requestDto.getUserId())
			.orElseThrow(() -> new IllegalArgumentException("유저 없음"));

		allProduct.forEach(product -> wonItemService.createWonItem(product, user));

		return CommonResponse.ok();
	}
}
