package com.example.auction.domain.order.handler;

import static com.example.auction.domain.wonitem.service.WonItemService.WON_ITEM_PREFIX;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import com.example.auction.common.handler.RedisKeyEventHandler;
import com.example.auction.domain.order.service.OrderService;

@Component
@RequiredArgsConstructor
public class AutoOrderEventHandler implements RedisKeyEventHandler {

	private final OrderService orderService;

	@Override
	public String getPrefix() {
		return WON_ITEM_PREFIX;
	}

	@Override
	public void handle(String key) {

		String[] split = key.split(":");
		long userId = Long.parseLong(split[1]);
		long productId = Long.parseLong(split[2]);
		orderService.saveAutoOder(userId, productId);
	}

}

