package ru.iprody.orderservice.integration.payment.messaging.dto;

public record PaymentResultMessage(
        Long orderId,
        Long paymentId,
        String status
) {
}
