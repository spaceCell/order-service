package com.example.orderservice.integration.payment.messaging;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import com.example.orderservice.application.payment.CreateOrderPaymentCommand;
import com.example.orderservice.domain.model.Order;
import com.example.orderservice.integration.payment.messaging.config.PaymentRabbitMqProperties;
import com.example.orderservice.integration.payment.messaging.dto.PaymentRequestMessage;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentRequestPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final PaymentRabbitMqProperties props;

    public void publish(Order order, CreateOrderPaymentCommand command) {
        PaymentRequestMessage message = new PaymentRequestMessage(
                order.getId(),
                order.getTotalAmount().getAmount(),
                order.getTotalAmount().getCurrency(),
                command.method().name()
        );
        rabbitTemplate.convertAndSend(props.exchangeRequestName(), props.queueRequestName(), message);
        log.info("Published payment request: orderId={}, amount={}", order.getId(), order.getTotalAmount().getAmount());
    }
}
