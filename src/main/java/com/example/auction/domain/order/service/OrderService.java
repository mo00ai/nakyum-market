package com.example.auction.domain.order.service;

import static com.example.auction.domain.product.exception.ProductErrorCode.PRODUCT_NOT_FOUND;
import static com.example.auction.domain.user.exception.ErrorCode.NOT_FOUND_USER;

import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.auction.common.exception.CustomException;
import com.example.auction.domain.order.dto.response.OrderResponseDto;
import com.example.auction.domain.order.entity.Order;
import com.example.auction.domain.order.entity.OrderItem;
import com.example.auction.domain.order.repository.OrderItemRepository;
import com.example.auction.domain.order.repository.OrderRepository;
import com.example.auction.domain.product.entity.Product;
import com.example.auction.domain.product.repository.ProductRepository;
import com.example.auction.domain.user.entity.User;
import com.example.auction.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	@Transactional
	@PreAuthorize("hasRole('ADMIN')")
	public OrderResponseDto orderSave(User loginUser, Long totalPrice, Long productId) {

		User findUser = userRepository.findById(loginUser.getId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER, NOT_FOUND_USER.getMessage()));

		Product findProduct = productRepository.findById(productId)
			.orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND, PRODUCT_NOT_FOUND.getMessage()));

		Order saveOrder = orderRepository.save(Order.of(findUser, totalPrice));
		orderItemRepository.save(OrderItem.of(saveOrder, findProduct));

		return OrderResponseDto.from(saveOrder, findUser, findProduct);
	}
}
