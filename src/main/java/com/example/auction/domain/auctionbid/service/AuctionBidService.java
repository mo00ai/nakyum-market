package com.example.auction.domain.auctionbid.service;

import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.example.auction.common.exception.CustomException;
import com.example.auction.common.exception.ErrorCode;
import com.example.auction.domain.auctionbid.dto.BidRedisDto;
import com.example.auction.domain.auctionbid.dto.request.BidRequestDto;
import com.example.auction.domain.auctionbid.repository.AuctionBidRepository;
import com.example.auction.domain.product.entity.Product;
import com.example.auction.domain.product.repository.ProductRepository;

@Service
@RequiredArgsConstructor
public class AuctionBidService {

	private final AuctionBidRepository auctionBidRepository;
	private final AuctionBidRedisService auctionBidRedisService;
	private final ProductRepository productRepository;

	public void bidSave(Long productId, Long userId, BidRequestDto requestDto) {

		Product product = productRepository.findById(productId).orElseThrow();
		Long bidPrice = requestDto.getBidPrice();

		if (bidPrice < product.getStartPrice()) {
			throw new CustomException(ErrorCode.BID_PRICE_BELOW_START);
		}

		LocalDateTime now = LocalDateTime.now();

		BidRedisDto dto = BidRedisDto.of(requestDto, product.getId(), userId, now);
		auctionBidRedisService.trySaveHighestBid(productId, dto);
	}
}
