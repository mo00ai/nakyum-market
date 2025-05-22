package com.example.auction.domain.wonitem.handler;

import org.springframework.stereotype.Component;

import com.example.auction.common.handler.RedisKeyEventHandler;

@Component
public class WonItemKeyEventHandler implements RedisKeyEventHandler {

	@Override
	public String getPrefix() {
		return "wonItem:";
	}

	@Override
	public void handle(String key) {

		//wonitem 이벤트 발생시
		//db 처리하는 로직

	}
}
