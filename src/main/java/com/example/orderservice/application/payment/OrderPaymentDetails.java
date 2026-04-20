package com.example.orderservice.application.payment;

import java.time.LocalDateTime;

import com.example.orderservice.application.dto.MoneyDetails;

public record OrderPaymentDetails(
        Long id,
        Long orderId,
        PaymentStatus status,
        PaymentMethod method,
        MoneyDetails amount,
        LocalDateTime createdAt
) {
}
