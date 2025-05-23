package com.example.auction.domain.product.service;

import static com.example.auction.domain.product.exception.ProductErrorCode.PRODUCT_NOT_FOUND;
import static com.example.auction.domain.user.exception.ErrorCode.NOT_FOUND_USER;

import com.example.auction.common.service.RedisService;
import com.example.auction.domain.auctionbid.entity.AuctionBid;
import com.example.auction.domain.auctionbid.service.AuctionBidService;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.auction.common.exception.CustomException;
import com.example.auction.common.response.PageResponse;
import com.example.auction.domain.image.entity.Image;
import com.example.auction.domain.image.service.ImageService;
import com.example.auction.domain.product.dto.request.ProductRequestDto;
import com.example.auction.domain.product.dto.request.ProductUpdateRequestDto;
import com.example.auction.domain.product.dto.response.ProductResponseDto;
import com.example.auction.domain.product.dto.response.ProductSaveResponseDto;
import com.example.auction.domain.product.dto.response.ProductWithdrawResponseDto;
import com.example.auction.domain.product.entity.Product;
import com.example.auction.domain.product.repository.ProductRepository;
import com.example.auction.domain.searchLog.service.SearchLogService;
import com.example.auction.domain.user.entity.User;
import com.example.auction.domain.user.repository.UserRepository;
import com.example.auction.domain.wonitem.service.WonItemService;

@Service
@RequiredArgsConstructor
public class ProductService {

	private final ProductRepository productRepository;
	private final UserRepository userRepository;
	private final ImageService imageService;
	private final WonItemService wonItemService;
	private final SearchLogService searchLogService;
	private final RedisService redisService;
	private final AuctionBidService auctionBidService;
	@Value("${file.upload-dir}")
	private String IMAGE_DIR;

	@Transactional
	public ProductSaveResponseDto saveProduct(String email, @Valid ProductRequestDto dto, List<MultipartFile> files) {

		User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(NOT_FOUND_USER));

		Image image = null;

		if (files != null && !files.isEmpty()) {
			image = imageService.uploadFile(files);
		}

		Product product = Product.of(dto.getName(), dto.getDescription(), dto.getStartPrice(), dto.getUnitPrice(),
			dto.getEndedAt(), image);

		Product savedProduct = productRepository.save(product);

		// 저장 후 레디스 올림
		Duration duration = Duration.between(LocalDateTime.now(),dto.getEndedAt());
		long seconds = duration.getSeconds();
		redisService.setKeyValue("auction:end:" + savedProduct.getId(),"", Duration.ofSeconds(seconds)); // 경매 종료 체크

		if (files != null && !files.isEmpty()) {
			imageService.uploadFile(files);
		}

		ProductSaveResponseDto responseDto = new ProductSaveResponseDto(savedProduct.getId());

		return responseDto;
	}

	@Transactional(readOnly = true)
	public ProductResponseDto findProduct(Long id, Long userId) {
		String countKey = "product:count:" + id;
		String lockKey = "product:"+ id +"lock:" + userId;

		Product product = productRepository.findByIdWithImage(id)
			.orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

		redisService.setIfAbsent(countKey, product.getCount(), Duration.ofMinutes(30));
		redisService.setProductCntExpire(countKey); // 남은시간 10분이하면 연장
		// 중복 조회 방지 (10분 동안 1회만 증가)

		long redisCount = redisService.setIfAbsent(lockKey, "", Duration.ofSeconds(10))
			? redisService.incrementValue(countKey)
			: redisService.getKeyLongValue(countKey);

		String uploadFileName = product.getImage().getUploadFileName();
		String imgUrl = IMAGE_DIR + uploadFileName;

        return ProductResponseDto.from(imgUrl, product, redisCount);
	}

	@Transactional(readOnly = true)
	public PageResponse<ProductResponseDto> findProducts(String keyword, int page) {

		int adjustedPage = (page > 0) ? page - 1 : 0;
		Pageable pageable = PageRequest.of(adjustedPage, 10);

		Page<ProductResponseDto> allPage = productRepository.findProducts(keyword, pageable, IMAGE_DIR);

		searchLogService.saveSearchLog(keyword);

		return PageResponse.from(allPage);

	}

	@Transactional
	public ProductResponseDto updateProduct(Long id, ProductUpdateRequestDto dto) {

		Product product = productRepository.findByIdWithImage(id)
			.orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

		product.updateProduct(dto.getName(), dto.getDescription());

		String uploadFileName = product.getImage().getUploadFileName();
		String imgUrl = IMAGE_DIR + uploadFileName;

		ProductResponseDto responseDto = ProductResponseDto.from(imgUrl, product, product.getCount());

		return responseDto;
	}
	@Transactional
	public void updateCount(Long id, Long count){

		Product product = productRepository.findByIdWithImage(id)
			.orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

		product.updateCount(count);
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
	public void updateFinalPrice(Long id, Long finalPrice, User user) {
		Product product = productRepository.findByIdWithImage(id)
			.orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND));

		product.updateFinalPrice(finalPrice);
		auctionBidService.update(id,finalPrice);
		wonItemService.createWonItem(product, user);    // 낙찰된 아이템 저장 로직 추가!!!~~~!!!~!~!!!

	}

}
