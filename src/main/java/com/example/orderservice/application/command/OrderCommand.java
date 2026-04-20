package com.example.orderservice.application.command;

import java.util.List;

import com.example.orderservice.application.dto.OrderItemDetails;
import com.example.orderservice.application.dto.ShippingAddressDetails;
import com.example.orderservice.domain.model.OrderStatus;

public record OrderCommand(
        Long customerId,
        OrderStatus status,
        ShippingAddressDetails shippingAddress,
        List<OrderItemDetails> items
) {
}
