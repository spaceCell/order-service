package com.example.orderservice.integration.payment;

import org.springframework.stereotype.Component;
import com.example.orderservice.application.dto.MoneyDetails;
import com.example.orderservice.application.payment.CreateOrderPaymentCommand;
import com.example.orderservice.application.payment.OrderPaymentDetails;
import com.example.orderservice.application.payment.PaymentStatus;
import com.example.orderservice.domain.model.Order;
import com.example.orderservice.integration.payment.dto.PaymentAmountRequest;
import com.example.orderservice.integration.payment.dto.PaymentCreateRequest;
import com.example.orderservice.integration.payment.dto.PaymentCreateResponse;

@Component
public class PaymentServiceMapper {

    public PaymentCreateRequest toPaymentCreateRequest(Order order, CreateOrderPaymentCommand command) {
        return new PaymentCreateRequest(
                order.getId(),
                PaymentStatus.PENDING,
                command.method(),
                new PaymentAmountRequest(
                        order.getTotalAmount().getAmount(),
                        order.getTotalAmount().getCurrency()
                )
        );
    }

    public OrderPaymentDetails toOrderPaymentDetails(PaymentCreateResponse paymentCreateResponse) {
        return new OrderPaymentDetails(
                paymentCreateResponse.id(),
                paymentCreateResponse.orderId(),
                paymentCreateResponse.status(),
                paymentCreateResponse.method(),
                new MoneyDetails(
                        paymentCreateResponse.amount().amount(),
                        paymentCreateResponse.amount().currency()
                ),
                paymentCreateResponse.createdAt()
        );
    }
}
