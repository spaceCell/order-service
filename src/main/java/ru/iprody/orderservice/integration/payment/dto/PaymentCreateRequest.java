package ru.iprody.orderservice.integration.payment.dto;

import ru.iprody.orderservice.application.payment.PaymentMethod;
import ru.iprody.orderservice.application.payment.PaymentStatus;

public record PaymentCreateRequest(
        Long orderId,
        PaymentStatus status,
        PaymentMethod method,
        PaymentAmountRequest amount
) {
}
