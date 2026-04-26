package ru.iprody.orderservice.integration.payment.dto;

import java.math.BigDecimal;

public record PaymentAmountResponse(
        BigDecimal amount,
        String currency
) {
}
