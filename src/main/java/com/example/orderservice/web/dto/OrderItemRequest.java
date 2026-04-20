package ru.iprody.orderservice.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Позиция заказа в запросе")
public class OrderItemRequest {

    private String productName;
    private Integer quantity;
    private MoneyRequest price;
}
