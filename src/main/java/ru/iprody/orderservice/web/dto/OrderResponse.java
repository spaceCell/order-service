package ru.iprody.orderservice.web.dto;

import java.time.LocalDateTime;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.iprody.orderservice.domain.model.OrderStatus;

@Data
@AllArgsConstructor
@Schema(description = "Ответ с данными заказа")
public class OrderResponse {

    @Schema(description = "Идентификатор заказа", example = "1")
    private Long id;
    @Schema(description = "Идентификатор клиента", example = "101")
    private Long customerId;
    @Schema(description = "Статус заказа", example = "CONFIRMED")
    private OrderStatus status;
    @Schema(description = "Дата и время создания заказа")
    private LocalDateTime createdAt;
    @Schema(description = "Адрес доставки")
    private ShippingAddressResponse shippingAddress;
    @Schema(description = "Общая сумма заказа")
    private MoneyResponse totalAmount;
    @Schema(description = "Позиции заказа")
    private List<OrderItemResponse> items;
}
