package com.example.auction.common.listener;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import com.example.auction.common.handler.RedisKeyEventHandler;

@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

	private final Map<String, RedisKeyEventHandler> handlerMap;

	public RedisKeyExpirationListener(
		RedisMessageListenerContainer listenerContainer, List<RedisKeyEventHandler> handlers

	) {
		super(listenerContainer);
		this.handlerMap = handlers.stream()
			.collect(Collectors.toMap(
				RedisKeyEventHandler::getPrefix,
				handler -> handler
			));
	}

	// 만료된 키 처리
	@Override
	public void onMessage(Message message, byte[] pattern) {

		String key = message.toString();
		handlerMap.entrySet().stream()
			.filter(entry -> key.startsWith(entry.getKey()))
			.findFirst()
			.map(
				Map.Entry::getValue)
			.ifPresent(handler -> handler.handle(key));
	}

}
