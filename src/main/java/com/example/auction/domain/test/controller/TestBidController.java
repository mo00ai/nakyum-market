package com.example.auction.domain.test.controller;

import com.example.auction.common.exception.CustomException;
import com.example.auction.common.exception.ErrorCode;
import com.example.auction.common.service.RedisService;
import com.example.auction.domain.auctionbid.dto.BidRedisDto;
import com.example.auction.domain.auctionbid.handler.AuctionBidKeyEventHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/bid")
@RequiredArgsConstructor
public class TestBidController {

    private final RedisService redisService;
    private final AuctionBidKeyEventHandler auctionBidKeyEventHandler;


    @GetMapping
    public void doBidTest() {
        String key = "auction:" + 1L + ":logs";
        BidRedisDto bidRedisDto = BidRedisDto.builder().bidPrice(1000L).bidTimeAt(LocalDateTime.now()).productId(1L).userId(1L)
            .build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // 시간 타입을 직렬화하려면 필요
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String json;
        try {
            json = objectMapper.writeValueAsString(bidRedisDto);
        } catch (JsonProcessingException e) {
            throw new CustomException(ErrorCode.REDIS_SERIALIZATION_ERROR);
        }
        redisService.addToZSet(key, json, System.currentTimeMillis()); // 2. 로그 기록
        auctionBidKeyEventHandler.handle(key);
    }
}
