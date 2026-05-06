package ru.iprody.orderservice.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.iprody.orderservice.application.payment.PaymentMethod;

@Data
@NoArgsConstructor
@Schema(description = "Запрос на создание платежа для существующего заказа")
public class OrderPaymentRequest {

    @Schema(description = "Способ оплаты", allowableValues = {"CARD", "CASH", "BANK_TRANSFER"}, example = "CARD")
    private PaymentMethod method;
}
