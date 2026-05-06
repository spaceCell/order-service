package ru.iprody.orderservice.integration.payment.dto;

import java.math.BigDecimal;

public record PaymentAmountRequest(
        BigDecimal amount,
        String currency
) {
}
