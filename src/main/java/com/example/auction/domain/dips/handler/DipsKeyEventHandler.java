package com.example.auction.domain.dips.handler;

import org.springframework.stereotype.Component;

import com.example.auction.common.handler.RedisKeyEventHandler;

@Component
public class DipsKeyEventHandler implements RedisKeyEventHandler {

	@Override
	public String getPrefix() {
		return "dips:";
	}

	@Override
	public void handle(String key) {

		//dips 이벤트 발생시
		//db 처리하는 로직

	}

}
