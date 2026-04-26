package ru.iprody.orderservice.web.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.iprody.orderservice.application.payment.PaymentMethod;
import ru.iprody.orderservice.application.payment.PaymentStatus;

@Data
@AllArgsConstructor
@Schema(description = "Ответ с данными созданного платежа")
public class OrderPaymentResponse {

    @Schema(description = "Идентификатор платежа", example = "1")
    private Long id;
    @Schema(description = "Идентификатор заказа", example = "1")
    private Long orderId;
    @Schema(description = "Статус платежа", example = "PENDING")
    private PaymentStatus status;
    @Schema(description = "Способ оплаты", example = "CARD")
    private PaymentMethod method;
    @Schema(description = "Сумма платежа")
    private MoneyResponse amount;
    @Schema(description = "Дата и время создания платежа")
    private LocalDateTime createdAt;
}
