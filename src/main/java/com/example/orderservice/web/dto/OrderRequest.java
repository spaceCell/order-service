package com.example.orderservice.web.dto;

import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.orderservice.domain.model.OrderStatus;

@Data
@NoArgsConstructor
@Schema(description = "Запрос на создание или обновление заказа")
public class OrderRequest {

    @Schema(description = "Идентификатор клиента", example = "101")
    private Long customerId;
    @Schema(description = "Статус заказа", example = "NEW")
    private OrderStatus status;
    @Schema(description = "Адрес доставки")
    private ShippingAddressRequest shippingAddress;
    @Schema(description = "Позиции заказа")
    private List<OrderItemRequest> items = new ArrayList<>();
}
