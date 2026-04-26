package ru.iprody.orderservice.web.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Сумма и валюта в ответе")
public class MoneyResponse {

    private BigDecimal amount;
    private String currency;
}
