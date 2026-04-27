package ru.iprody.orderservice.integration.payment.messaging.dto;

import java.math.BigDecimal;

public record PaymentRequestMessage(
        Long orderId,
        BigDecimal amount,
        String currency,
        String method
) {
}
