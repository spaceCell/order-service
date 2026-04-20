package com.example.orderservice.application;

import java.util.Collections;
import java.util.List;

import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.orderservice.application.command.OrderCommand;
import com.example.orderservice.application.dto.MoneyDetails;
import com.example.orderservice.application.dto.OrderDetails;
import com.example.orderservice.application.dto.OrderItemDetails;
import com.example.orderservice.application.dto.ShippingAddressDetails;
import com.example.orderservice.application.payment.CreateOrderPaymentCommand;
import com.example.orderservice.application.payment.OrderPaymentDetails;
import com.example.orderservice.common.ResourceNotFoundException;
import com.example.orderservice.common.PaymentServiceException;
import com.example.orderservice.domain.model.Money;
import com.example.orderservice.domain.model.Order;
import com.example.orderservice.domain.model.OrderItem;
import com.example.orderservice.domain.model.ShippingAddress;
import com.example.orderservice.domain.repository.OrderRepository;
import com.example.orderservice.integration.payment.PaymentClientAdapter;
import com.example.orderservice.integration.payment.PaymentServiceMapper;
import com.example.orderservice.integration.payment.messaging.PaymentRequestPublisher;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderApplicationService {

    private final OrderRepository orderRepository;
    private final PaymentClientAdapter paymentClientAdapter;
    private final PaymentServiceMapper paymentServiceMapper;
    private final PaymentRequestPublisher paymentRequestPublisher;

    @Transactional
    @CircuitBreaker(name = "orderServiceCircuitBreaker")
    public OrderDetails create(OrderCommand orderCommand) {
        Order order = new Order(
                orderCommand.customerId(),
                orderCommand.status(),
                toShippingAddress(orderCommand.shippingAddress()),
                toOrderItems(orderCommand.items())
        );
        return toOrderDetails(orderRepository.save(order));
    }

    @CircuitBreaker(name = "orderServiceCircuitBreaker")
    public List<OrderDetails> getAll() {
        return orderRepository.findAll()
                .stream()
                .map(this::toOrderDetails)
                .toList();
    }

    @CircuitBreaker(name = "orderServiceCircuitBreaker")
    public OrderDetails getById(Long orderId) {
        return toOrderDetails(getOrder(orderId));
    }

    @Transactional
    @CircuitBreaker(name = "orderServiceCircuitBreaker")
    public OrderDetails update(Long orderId, OrderCommand orderCommand) {
        Order order = getOrder(orderId);
        order.update(
                orderCommand.customerId(),
                orderCommand.status(),
                toShippingAddress(orderCommand.shippingAddress()),
                toOrderItems(orderCommand.items())
        );
        return toOrderDetails(order);
    }

    @Transactional
    @CircuitBreaker(name = "orderServiceCircuitBreaker")
    public void delete(Long orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("Order with id " + orderId + " was not found");
        }
        orderRepository.deleteById(orderId);
    }

    @Transactional
    public OrderPaymentDetails createPayment(Long orderId, CreateOrderPaymentCommand createOrderPaymentCommand) {
        if (createOrderPaymentCommand == null || createOrderPaymentCommand.method() == null) {
            throw new IllegalArgumentException("Payment method must be provided");
        }

        Order order = getOrder(orderId);
        try {
            return paymentServiceMapper.toOrderPaymentDetails(
                    paymentClientAdapter.createPayment(
                            paymentServiceMapper.toPaymentCreateRequest(order, createOrderPaymentCommand)
                    )
            );
        } catch (RequestNotPermitted | BulkheadFullException exception) {
            throw exception;
        } catch (RuntimeException exception) {
            throw new PaymentServiceException(
                    "Payment service request failed: " + exception.getMessage(),
                    exception
            );
        }
    }

    @Transactional
    public OrderDetails requestPaymentAsync(Long orderId, CreateOrderPaymentCommand command) {
        if (command == null || command.method() == null) {
            throw new IllegalArgumentException("Payment method must be provided");
        }
        Order order = getOrder(orderId);
        paymentRequestPublisher.publish(order, command);
        return toOrderDetails(order);
    }

    private Order getOrder(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order with id " + orderId + " was not found"));
    }

    private ShippingAddress toShippingAddress(ShippingAddressDetails shippingAddressDetails) {
        if (shippingAddressDetails == null) {
            throw new IllegalArgumentException("Shipping address must be provided");
        }
        return new ShippingAddress(
                shippingAddressDetails.street(),
                shippingAddressDetails.city(),
                shippingAddressDetails.postalCode(),
                shippingAddressDetails.country()
        );
    }

    private List<OrderItem> toOrderItems(List<OrderItemDetails> orderItemDetailsList) {
        if (orderItemDetailsList == null) {
            return Collections.emptyList();
        }
        return orderItemDetailsList.stream()
                .map(orderItemDetails -> new OrderItem(
                        orderItemDetails.productName(),
                        orderItemDetails.quantity(),
                        toMoney(orderItemDetails)
                ))
                .toList();
    }

    private Money toMoney(OrderItemDetails orderItemDetails) {
        if (orderItemDetails == null || orderItemDetails.price() == null) {
            throw new IllegalArgumentException("Each order item must contain price");
        }
        return new Money(orderItemDetails.price().amount(), orderItemDetails.price().currency());
    }

    private OrderDetails toOrderDetails(Order order) {
        return new OrderDetails(
                order.getId(),
                order.getCustomerId(),
                order.getStatus(),
                order.getCreatedAt(),
                new ShippingAddressDetails(
                        order.getShippingAddress().getStreet(),
                        order.getShippingAddress().getCity(),
                        order.getShippingAddress().getPostalCode(),
                        order.getShippingAddress().getCountry()
                ),
                new MoneyDetails(
                        order.getTotalAmount().getAmount(),
                        order.getTotalAmount().getCurrency()
                ),
                order.getItems()
                        .stream()
                        .map(orderItem -> new OrderItemDetails(
                                orderItem.getId(),
                                orderItem.getProductName(),
                                orderItem.getQuantity(),
                                new MoneyDetails(orderItem.getPrice().getAmount(), orderItem.getPrice().getCurrency())
                        ))
                        .toList()
        );
    }
}
