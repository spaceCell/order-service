package ru.iprody.orderservice.application.dto;

import java.math.BigDecimal;

public record MoneyDetails(
        BigDecimal amount,
        String currency
) {
}
