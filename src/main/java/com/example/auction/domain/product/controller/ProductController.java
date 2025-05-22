package com.example.auction.domain.product.controller;

import java.util.List;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.auction.common.annotation.ImageValid;
import com.example.auction.common.response.CommonResponse;
import com.example.auction.common.response.PageResponse;
import com.example.auction.domain.auth.security.CustomUserDetails;
import com.example.auction.domain.product.dto.request.ProductRequestDto;
import com.example.auction.domain.product.dto.request.ProductUpdateRequestDto;
import com.example.auction.domain.product.dto.response.PageResponseDto;
import com.example.auction.domain.product.dto.response.ProductResponseDto;
import com.example.auction.domain.product.dto.response.ProductSaveResponseDto;
import com.example.auction.domain.product.dto.response.ProductWithdrawResponseDto;
import com.example.auction.domain.product.service.ProductService;
import com.example.auction.domain.user.auth.security.CustomUserDetails;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

	private final ProductService productService;

	@PreAuthorize("hasRole('ADMIN')")
	@ImageValid
	@PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public CommonResponse<ProductSaveResponseDto> saveProduct(@AuthenticationPrincipal CustomUserDetails userDetail,
		@Valid @RequestPart("dto") ProductRequestDto dto,
		@RequestPart(value = "files", required = false) List<MultipartFile> files) {

		return CommonResponse.created(productService.saveProduct(userDetail.getUsername(), dto, files));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}")
	public CommonResponse<ProductResponseDto> updateProduct(@AuthenticationPrincipal CustomUserDetails userDetail,
		@PathVariable Long id, @Valid @RequestBody ProductUpdateRequestDto dto) {

		return CommonResponse.ok(productService.updateProduct(id, dto));

	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public CommonResponse<ProductWithdrawResponseDto> deleteProduct(
		@AuthenticationPrincipal CustomUserDetails userDetail,
		@PathVariable Long id) {

		return CommonResponse.ok(productService.deleteProduct(id));
	}

	@GetMapping("/{id}")
	public CommonResponse<ProductResponseDto> findProduct(@AuthenticationPrincipal CustomUserDetails userDetail,
		@PathVariable Long id) {

		return CommonResponse.ok(productService.findProduct(id));
	}

	//검색 v1 (아무것도 하지 않은 api)
	@GetMapping("/v1")
	public CommonResponse<PageResponseDto> findProducts(@AuthenticationPrincipal CustomUserDetails userDetail,
		@RequestParam(required = false) String keyword,
		@RequestParam(defaultValue = "1") int page) {

		return CommonResponse.ok(productService.findProducts(keyword, page));
	}

}














