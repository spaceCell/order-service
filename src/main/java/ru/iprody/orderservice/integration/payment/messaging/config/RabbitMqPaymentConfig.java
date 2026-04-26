package ru.iprody.orderservice.integration.payment.messaging.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.iprody.orderservice.integration.payment.messaging.dto.PaymentRequestMessage;
import ru.iprody.orderservice.integration.payment.messaging.dto.PaymentResultMessage;

import java.util.Map;

@Configuration
@EnableConfigurationProperties(PaymentRabbitMqProperties.class)
public class RabbitMqPaymentConfig {

    private final PaymentRabbitMqProperties props;

    public RabbitMqPaymentConfig(PaymentRabbitMqProperties props) {
        this.props = props;
    }

    // --- Request (order-service → payment-service) ---

    @Bean
    public Queue paymentRequestQueue() {
        return QueueBuilder.durable(props.queueRequestName()).build();
    }

    @Bean
    public DirectExchange paymentRequestExchange() {
        return new DirectExchange(props.exchangeRequestName());
    }

    @Bean
    public Binding paymentRequestBinding(Queue paymentRequestQueue, DirectExchange paymentRequestExchange) {
        return BindingBuilder.bind(paymentRequestQueue).to(paymentRequestExchange).with(props.queueRequestName());
    }

    // --- Result (payment-service → order-service) ---

    @Bean
    public Queue paymentResultQueue() {
        return QueueBuilder.durable(props.queueResultName()).build();
    }

    @Bean
    public DirectExchange paymentResultExchange() {
        return new DirectExchange(props.exchangeResultName());
    }

    @Bean
    public Binding paymentResultBinding(Queue paymentResultQueue, DirectExchange paymentResultExchange) {
        return BindingBuilder.bind(paymentResultQueue).to(paymentResultExchange).with(props.queueResultName());
    }

    // --- Infrastructure ---

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        converter.setClassMapper(classMapper());
        return converter;
    }

    @Bean
    public DefaultClassMapper classMapper() {
        DefaultClassMapper mapper = new DefaultClassMapper();
        mapper.setIdClassMapping(Map.of(
                "payment-request", PaymentRequestMessage.class,
                "payment-result", PaymentResultMessage.class
        ));
        return mapper;
    }
}
