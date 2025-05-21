package com.example.auction.domain.product.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.auction.domain.product.dto.response.ProductResponseDto;

public interface ProductRepositoryCustom {
	Page<ProductResponseDto> findProducts(String keyword, Pageable pageable, String IMAGE_DIR);
}
