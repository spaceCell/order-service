package com.example.orderservice.integration.payment.dto;

import com.example.orderservice.application.payment.PaymentMethod;
import com.example.orderservice.application.payment.PaymentStatus;

public record PaymentCreateRequest(
        Long orderId,
        PaymentStatus status,
        PaymentMethod method,
        PaymentAmountRequest amount
) {
}
