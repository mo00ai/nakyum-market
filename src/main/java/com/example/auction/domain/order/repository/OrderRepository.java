package com.example.auction.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.auction.domain.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
