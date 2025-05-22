package com.example.auction.common.listener;

import static com.example.auction.domain.wonitem.service.WonItemService.WON_ITEM_PREFIX;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import com.example.auction.domain.order.service.OrderService;

@Component
public class AutoOrderListener extends KeyExpirationEventMessageListener {

	private final OrderService orderService;

	public AutoOrderListener(RedisMessageListenerContainer listenerContainer, OrderService orderService) {
		super(listenerContainer);
		this.orderService = orderService;
	}

	// 만료된 키 처리
	@Override
	public void onMessage(Message message, byte[] pattern) {
		String key = message.toString();
		if (key.startsWith(WON_ITEM_PREFIX)) {
			String[] split = key.split(":");
			long userId = Long.parseLong(split[1]);
			long productId = Long.parseLong(split[2]);
			orderService.saveAutoOder(userId, productId);
		}
	}
}
