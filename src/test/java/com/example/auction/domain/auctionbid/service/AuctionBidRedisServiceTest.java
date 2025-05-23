package com.example.auction.domain.auctionbid.service;

import static com.example.auction.domain.auctionbid.exception.AuctionBidErrorCode.BID_PRICE_BELOW_HIGHEST;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.auction.common.exception.CustomException;
import com.example.auction.common.exception.ErrorCode;
import com.example.auction.domain.auctionbid.dto.BidRedisDto;
import com.example.auction.domain.auctionbid.dto.request.BidRequestDto;

@ActiveProfiles("test")
@SpringBootTest
public class AuctionBidRedisServiceTest {

	@Autowired
	private AuctionBidRedisService auctionBidRedisService;

	private final Long productId = 1000L;

	@BeforeEach
	void clearRedisBeforeEachTest() {
		auctionBidRedisService.clearKey(productId);
	}

	@Test
	void 저장된_입찰이_정상적으로_조회된다() {
		// given
		BidRequestDto bidRequest = new BidRequestDto(15000L);
		BidRedisDto dto = BidRedisDto.of(bidRequest, productId, 1L, LocalDateTime.now());

		// when
		assertDoesNotThrow(() -> auctionBidRedisService.trySaveHighestBid(productId, dto));

		// then
		Optional<BidRedisDto> result = auctionBidRedisService.getHighestBid(productId);
		assertThat(result).isPresent();
		assertThat(result.get().getBidPrice()).isEqualTo(15000L);
	}

	@Test
	void 낮은_입찰가는_예외가_발생한다() {
		// given
		BidRedisDto highDto = BidRedisDto.of(new BidRequestDto(15000L), productId, 1L, LocalDateTime.now());
		auctionBidRedisService.trySaveHighestBid(productId, highDto);

		BidRedisDto lowDto = BidRedisDto.of(new BidRequestDto(14000L), productId, 2L, LocalDateTime.now());

		// when & then
		assertThatThrownBy(() -> auctionBidRedisService.trySaveHighestBid(productId, lowDto))
			.isInstanceOf(CustomException.class)
			.hasMessageContaining("입찰 금액은 현재 최고가보다 높아야 합니다.");
	}

	@Test
	void 동시에_동일가격입찰_요청시_단하나만_성공한다() throws InterruptedException {
		// given
		int threadCount = 10;
		long bidPrice = 15000L;
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);
		AtomicInteger successCount = new AtomicInteger(0);
		AtomicInteger failCount = new AtomicInteger(0);

		// when
		for (int i = 0; i < threadCount; i++) {
			final Long userId = ThreadLocalRandom.current().nextLong(1, 100);
			executor.submit(() -> {
				try {
					BidRequestDto bidRequest = new BidRequestDto(bidPrice);
					BidRedisDto dto = BidRedisDto.of(bidRequest, productId, userId, LocalDateTime.now());
					auctionBidRedisService.trySaveHighestBid(productId, dto);
					successCount.incrementAndGet();
				} catch (CustomException e) {
					if (e.getBaseCode() == BID_PRICE_BELOW_HIGHEST) {
						failCount.incrementAndGet();
					} else {
						throw e;
					}
				} finally {
					latch.countDown();
				}
			});
		}
		latch.await();

		// then
		assertThat(successCount.get()).isEqualTo(1);
		assertThat(failCount.get()).isEqualTo(9);
	}
}
