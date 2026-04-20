package com.example.orderservice.web.dto;

import java.math.BigDecimal;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Сумма и валюта в запросе")
public class MoneyRequest {

    private BigDecimal amount;
    private String currency;
}
