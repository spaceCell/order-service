package com.example.orderservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.orderservice.domain.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
