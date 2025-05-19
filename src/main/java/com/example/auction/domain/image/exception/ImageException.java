package com.example.auction.domain.image.exception;

import com.example.auction.common.exception.BaseCode;
import com.example.auction.common.exception.CustomException;

public class ImageException extends CustomException {
	public ImageException(BaseCode baseCode) {
		super(baseCode);
	}

	public ImageException(BaseCode baseCode, String message) {
		super(baseCode, message);
	}
}
