package com.example.auction.common.listener;

import com.example.auction.common.service.RedisService;
import com.example.auction.domain.test.TestRequestDto;
import com.example.auction.domain.test.service.TestService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;


@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    public RedisKeyExpirationListener(
        RedisMessageListenerContainer listenerContainer, RedisService redisService ,
        TestService testService
    ) {
        super(listenerContainer);
        this.redisService = redisService;
        this.testService = testService;
    }

    private final RedisService redisService;
    private final TestService testService;

    // 만료된 키 처리
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = message.toString();

        System.out.println("1111111");
        if(key.startsWith("AuctionBid:")){
            redisService.getKeyValue(key);
            // dto 꺼내왔다고 가정
            List<TestRequestDto> list = List.of(TestRequestDto.of(10000L,true, LocalDateTime.now(),1L,1L));
            testService.batchSave(list);
        }
    }
}
