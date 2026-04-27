package ru.iprody.orderservice.application.payment;

import java.time.LocalDateTime;

import ru.iprody.orderservice.application.dto.MoneyDetails;

public record OrderPaymentDetails(
        Long id,
        Long orderId,
        PaymentStatus status,
        PaymentMethod method,
        MoneyDetails amount,
        LocalDateTime createdAt
) {
}
