package com.example.auction.common.listener;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;


@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    public RedisKeyExpirationListener(
        RedisMessageListenerContainer listenerContainer
    ) {
        super(listenerContainer);
    }

    // 만료된 키 처리
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String key = message.toString();
    }
}
