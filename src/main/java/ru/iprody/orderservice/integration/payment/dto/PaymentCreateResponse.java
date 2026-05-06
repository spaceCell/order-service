package ru.iprody.orderservice.integration.payment.dto;

import java.time.LocalDateTime;

import ru.iprody.orderservice.application.payment.PaymentMethod;
import ru.iprody.orderservice.application.payment.PaymentStatus;

public record PaymentCreateResponse(
        Long id,
        Long orderId,
        PaymentStatus status,
        PaymentMethod method,
        PaymentAmountResponse amount,
        LocalDateTime createdAt
) {
}
