package com.example.orderservice.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Позиция заказа в ответе")
public class OrderItemResponse {

    private Long id;
    private String productName;
    private Integer quantity;
    private MoneyResponse price;
}
