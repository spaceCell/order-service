package ru.iprody.orderservice.application.payment;

public record CreateOrderPaymentCommand(
        PaymentMethod method
) {
}
