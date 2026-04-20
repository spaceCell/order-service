package com.example.orderservice.application.payment;

public record CreateOrderPaymentCommand(
        PaymentMethod method
) {
}
