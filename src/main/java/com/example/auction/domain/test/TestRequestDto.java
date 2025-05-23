package com.example.auction.domain.test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TestRequestDto {

    private Long productId;

    private Long userId;

    private Long bidPrice;

    private LocalDateTime bidTimeAt;

    public static TestRequestDto of(TestRequestDto bidRequestDto, Long productId, Long userId, LocalDateTime bidTimeAt) {
        return TestRequestDto.builder()
            .productId(productId)
            .userId(userId)
            .bidPrice(bidRequestDto.getBidPrice())
            .bidTimeAt(bidTimeAt)
            .build();
    }

}
