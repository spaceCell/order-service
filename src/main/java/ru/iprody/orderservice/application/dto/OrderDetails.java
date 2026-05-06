package ru.iprody.orderservice.application.dto;

import java.time.LocalDateTime;
import java.util.List;

import ru.iprody.orderservice.domain.model.OrderStatus;

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
