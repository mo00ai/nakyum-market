package com.example.auction.domain.auctionbid.handler;

import static com.example.auction.domain.user.exception.ErrorCode.NOT_FOUND_USER;

import com.example.auction.common.exception.CustomException;
import com.example.auction.common.handler.RedisKeyEventHandler;
import com.example.auction.common.service.RedisService;
import com.example.auction.domain.product.service.ProductService;
import com.example.auction.domain.test.TestRequestDto;
import com.example.auction.domain.test.repository.AuctionBidJdbcRepository;
import com.example.auction.domain.test.service.TestService;
import com.example.auction.domain.user.entity.User;
import com.example.auction.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionBidKeyEventHandler implements RedisKeyEventHandler {

    private final RedisService redisService;
    private final ProductService productService;
    private final AuctionBidJdbcRepository auctionBidJdbcRepository;
    private final UserRepository userRepository;

    @Override
    public String getPrefix() {
        return "auction:";
    }

    //redis 데이터 세팅에 따라 수정 예정
    @Override
    public void handle(String key) {
        // redis 에서 가져오기
//        String json = redisService.getKeyValueAsString(key);
//
//        if (json == null) {
//            return Optional.empty();
//        }
//        try {
//            BidRedisDto dto = objectMapper.readValue(json, BidRedisDto.class);
//            return Optional.of(dto);
//        } catch (JsonProcessingException e) {
//            throw new CustomException(ErrorCode.REDIS_DESERIALIZATION_ERROR);
//        }
        Set<TypedTuple<Object>> tuples = redisService.getZSetReversData(key);

        if (key.endsWith(":highest")) {
            TestRequestDto testRequestDto = tuples.stream()
                .map(tuple -> (TestRequestDto) tuple.getValue())
                .findFirst()
                .orElse(null);

            User user = userRepository.findById(testRequestDto.getUserId())
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER, NOT_FOUND_USER.getMessage()));

            productService.updateFinalPrice(testRequestDto.getProductId(),
                testRequestDto.getBidPrice(), user);
        }

        if (key.endsWith(":logs")) {
            // 타입 변환
            List<TestRequestDto> dtoList = new java.util.ArrayList<>(tuples.stream()
                .map(tuple -> (TestRequestDto) tuple.getValue())
                .toList());
            // dto 꺼내왔다고 가정
            for (int i = 0; i < 100; i++) {
                dtoList.add(
                    TestRequestDto.builder().bidPrice(10000L).bidTimeAt(LocalDateTime.now()).productId(1L).userId(1L)
                        .build());
            }
            auctionBidJdbcRepository.batchInsert(dtoList);
        }

    }
}
