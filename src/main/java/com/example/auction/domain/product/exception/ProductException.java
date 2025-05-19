package com.example.auction.domain.product.exception;

import com.example.auction.common.exception.BaseCode;
import com.example.auction.common.exception.CustomException;

public class ProductException extends CustomException {
	public ProductException(BaseCode baseCode) {
		super(baseCode);
	}

	public ProductException(BaseCode baseCode, String message) {
		super(baseCode, message);
	}
}
