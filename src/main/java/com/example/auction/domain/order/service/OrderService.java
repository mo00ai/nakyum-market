package com.example.auction.domain.order.service;

import static com.example.auction.domain.product.exception.ProductErrorCode.*;
import static com.example.auction.domain.user.exception.ErrorCode.*;

import java.util.List;

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
import com.example.auction.domain.wonitem.service.WonItemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final ProductRepository productRepository;
	private final UserRepository userRepository;

	private final WonItemService wonItemService;

	@Transactional
	public OrderResponseDto saveOrder(User loginUser, List<Long> productIds) {

		User findUser = userRepository.findById(loginUser.getId())
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER, NOT_FOUND_USER.getMessage()));

		wonItemService.validateMyWonItem(findUser.getId(), productIds);

		List<Product> products = productRepository.findAllByIdInAndFinalPriceIsNotNull(productIds);

		long totalPrice = products.stream().mapToLong(Product::getFinalPrice).sum();

		Order saveOrder = orderRepository.save(Order.of(findUser, totalPrice));

		List<OrderItem> orderItems = products.stream()
			.map(p -> OrderItem.of(saveOrder, p))
			.toList();

		List<OrderItem> savedItems = orderItemRepository.saveAll(orderItems);

		wonItemService.deleteWonItems(findUser.getId(), productIds);

		return OrderResponseDto.from(saveOrder, findUser, savedItems);
	}

	@Transactional
	public void saveAutoOrder(Long UserId, Long productId) {

		User findUser = userRepository.findById(UserId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER, NOT_FOUND_USER.getMessage()));

		Product product = productRepository.findAllByIdAndFinalPriceIsNotNull(productId)
			.orElseThrow(() -> new CustomException(PRODUCT_NOT_FOUND, PRODUCT_NOT_FOUND.getMessage()));

		Order saveOrder = orderRepository.save(Order.of(findUser, product.getFinalPrice()));

		boolean exists = orderItemRepository.existsByProductId(productId);
		if (!exists) {
			orderItemRepository.save(OrderItem.of(saveOrder, product));
		}
	}
}
