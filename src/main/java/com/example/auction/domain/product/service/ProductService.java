package com.example.auction.domain.product.service;

import static com.example.auction.domain.product.exception.ProductErrorCode.*;
import static com.example.auction.domain.user.exception.ErrorCode.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.auction.common.exception.CustomException;
import com.example.auction.domain.image.service.ImageService;
import com.example.auction.domain.product.dto.request.ProductRequestDto;
import com.example.auction.domain.product.dto.request.ProductUpdateRequestDto;
import com.example.auction.domain.product.dto.response.PageResponseDto;
import com.example.auction.domain.product.dto.response.ProductResponseDto;
import com.example.auction.domain.product.dto.response.ProductSaveResponseDto;
import com.example.auction.domain.product.dto.response.ProductWithdrawResponseDto;
import com.example.auction.domain.product.entity.Product;
import com.example.auction.domain.product.repository.ProductRepository;
import com.example.auction.domain.user.entity.User;
import com.example.auction.domain.user.repository.UserRepository;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final ImageService imageService;
	@Value("${file.upload-dir}")
	private String IMAGE_DIR;

	@Transactional
	public ProductSaveResponseDto saveProduct(String email, @Valid ProductRequestDto dto, List<MultipartFile> files) {

		User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(NOT_FOUND_USER));

		Product product = Product.of(dto.getName(), dto.getDescription(), dto.getStartPrice(), dto.getUnitPrice(),
			dto.getStartedAt(), dto.getEndedAt());

		Product savedProduct = productRepository.save(product);

		if (files != null && !files.isEmpty()) {
			imageService.uploadFile(files);
		}

		ProductSaveResponseDto responseDto = new ProductSaveResponseDto(savedProduct.getId());

		return responseDto;
	}

	@Transactional(readOnly = true)
	public ProductResponseDto findProduct(Long id) {

		Product product = productRepository.findByIdWithImage(id)
			.orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

		product.addCount();

		String uploadFileName = product.getImage().getUploadFileName();
		String imgUrl = IMAGE_DIR + uploadFileName;

		ProductResponseDto dto = ProductResponseDto.from(imgUrl, product);

		return dto;
	}

	public PageResponseDto findProducts(String keyword, int page) {

		int adjustedPage = (page > 0) ? page - 1 : 0;
		Pageable pageable = PageRequest.of(adjustedPage, 10);

		Page<ProductResponseDto> allPage = productRepository.findProducts(keyword, pageable, IMAGE_DIR);

		return PageResponseDto.from(allPage);

	}

	@Transactional
	public ProductResponseDto updateProduct(Long id, ProductUpdateRequestDto dto) {

		Product product = productRepository.findByIdWithImage(id)
			.orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

		product.updateProduct(dto.getName(), dto.getDescription());

		String uploadFileName = product.getImage().getUploadFileName();
		String imgUrl = IMAGE_DIR + uploadFileName;

		ProductResponseDto responseDto = ProductResponseDto.from(imgUrl, product);

		return responseDto;
	}

	@Transactional
	public ProductWithdrawResponseDto deleteProduct(Long id) {

		Product product = productRepository.findByIdWithImage(id)
			.orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

		product.deleteProduct();

		ProductWithdrawResponseDto dto = ProductWithdrawResponseDto.from(product, "상품이 삭제되었습니다.");

		return dto;
	}

	//Long id = product의 id
	//finalPrice = 낙찰가
	@Transactional
	public void updateFinalPrice(Long id, Long finalPrice) {
		Product product = productRepository.findByIdWithImage(id)
			.orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

		product.updateFinalPrice(finalPrice);

	}

}
