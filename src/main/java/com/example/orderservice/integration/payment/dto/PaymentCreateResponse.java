package com.example.orderservice.integration.payment.dto;

import java.time.LocalDateTime;

import com.example.orderservice.application.payment.PaymentMethod;
import com.example.orderservice.application.payment.PaymentStatus;

public record PaymentCreateResponse(
        Long id,
        Long orderId,
        PaymentStatus status,
        PaymentMethod method,
        PaymentAmountResponse amount,
        LocalDateTime createdAt
) {
}
