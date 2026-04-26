package ru.iprody.orderservice.integration.payment.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ru.iprody.orderservice.integration.payment.messaging.dto.PaymentResultMessage;

@Slf4j
@Component
public class PaymentResultListener {

    @RabbitListener(queues = "${rabbitmq.payment.queue-result-name}")
    public void handle(PaymentResultMessage message) {
        log.info("Received payment result: orderId={}, paymentId={}, status={}",
                message.orderId(), message.paymentId(), message.status());
    }
}
