package ru.iprody.orderservice.integration.payment;

import org.springframework.stereotype.Component;
import ru.iprody.orderservice.application.dto.MoneyDetails;
import ru.iprody.orderservice.application.payment.CreateOrderPaymentCommand;
import ru.iprody.orderservice.application.payment.OrderPaymentDetails;
import ru.iprody.orderservice.application.payment.PaymentStatus;
import ru.iprody.orderservice.domain.model.Order;
import ru.iprody.orderservice.integration.payment.dto.PaymentAmountRequest;
import ru.iprody.orderservice.integration.payment.dto.PaymentCreateRequest;
import ru.iprody.orderservice.integration.payment.dto.PaymentCreateResponse;

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
