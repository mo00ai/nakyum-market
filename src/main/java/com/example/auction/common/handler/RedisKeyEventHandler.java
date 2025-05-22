package com.example.auction.common.handler;

public interface RedisKeyEventHandler {
	String getPrefix();

	void handle(String key);
}
