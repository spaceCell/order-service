package com.example.orderservice.application.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.orderservice.domain.model.OrderStatus;

public record OrderDetails(
        Long id,
        Long customerId,
        OrderStatus status,
        LocalDateTime createdAt,
        ShippingAddressDetails shippingAddress,
        MoneyDetails totalAmount,
        List<OrderItemDetails> items
) {
}
