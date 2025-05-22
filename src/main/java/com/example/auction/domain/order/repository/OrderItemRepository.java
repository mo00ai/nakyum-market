package com.example.auction.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.auction.domain.order.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
	boolean existsByProductId(Long productId);
}
