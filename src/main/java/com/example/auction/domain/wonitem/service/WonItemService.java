package com.example.auction.domain.wonitem.service;

import java.time.Duration;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.example.auction.common.service.RedisService;
import com.example.auction.domain.product.entity.Product;
import com.example.auction.domain.user.entity.User;
import com.example.auction.domain.wonitem.entity.WonItem;

@Service
@RequiredArgsConstructor
public class WonItemService {

	private final static String WON_ITEM_PREFIX = "wonItem:";

	private final RedisService redisService;

	public void createWonItem(Product product, User user) {

		String key = WON_ITEM_PREFIX + user.getId() + ":" + product.getId();

		WonItem wonItem = WonItem.of(
			key,
			user.getId(),
			product.getId(),
			product.getFinalPrice()
		);

		redisService.setKeyValue(key, wonItem, Duration.ofHours(72));
	}

}
