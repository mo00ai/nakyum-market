package com.example.auction.common.listener;

import com.example.auction.common.service.RedisService;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;


@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    public RedisKeyExpirationListener(
        RedisMessageListenerContainer listenerContainer, RedisService redisService
    ) {
        super(listenerContainer);
        this.redisService = redisService;
    }

    private final RedisService redisService;

    // 만료된 키 처리
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = message.toString();
        System.out.println("1111111");
        if(key.startsWith("dips:")){

            redisService.getKeyValue(key);
        }
    }
}
