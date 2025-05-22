package com.example.auction.common.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FilterErrorDto {
	private final int statusCode;
	private final String message;
	private final LocalDateTime errorTime;
	private final String path;

}
