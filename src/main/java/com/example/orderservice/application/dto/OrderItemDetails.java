package com.example.orderservice.application.dto;

public record OrderItemDetails(
        Long id,
        String productName,
        Integer quantity,
        MoneyDetails price
) {
}
