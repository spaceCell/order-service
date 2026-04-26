package ru.iprody.orderservice.web.mapper;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;
import ru.iprody.orderservice.application.command.OrderCommand;
import ru.iprody.orderservice.application.dto.MoneyDetails;
import ru.iprody.orderservice.application.dto.OrderDetails;
import ru.iprody.orderservice.application.dto.OrderItemDetails;
import ru.iprody.orderservice.application.dto.ShippingAddressDetails;
import ru.iprody.orderservice.application.payment.CreateOrderPaymentCommand;
import ru.iprody.orderservice.application.payment.OrderPaymentDetails;
import ru.iprody.orderservice.web.dto.MoneyRequest;
import ru.iprody.orderservice.web.dto.MoneyResponse;
import ru.iprody.orderservice.web.dto.OrderPaymentRequest;
import ru.iprody.orderservice.web.dto.OrderPaymentResponse;
import ru.iprody.orderservice.web.dto.OrderItemRequest;
import ru.iprody.orderservice.web.dto.OrderItemResponse;
import ru.iprody.orderservice.web.dto.OrderRequest;
import ru.iprody.orderservice.web.dto.OrderResponse;
import ru.iprody.orderservice.web.dto.ShippingAddressRequest;
import ru.iprody.orderservice.web.dto.ShippingAddressResponse;

@Component
public class OrderWebMapper {

    public OrderCommand toOrderCommand(OrderRequest orderRequest) {
        return new OrderCommand(
                orderRequest.getCustomerId(),
                orderRequest.getStatus(),
                toShippingAddressDetails(orderRequest.getShippingAddress()),
                toOrderItemDetails(orderRequest.getItems())
        );
    }

    public OrderResponse toOrderResponse(OrderDetails orderDetails) {
        return new OrderResponse(
                orderDetails.id(),
                orderDetails.customerId(),
                orderDetails.status(),
                orderDetails.createdAt(),
                toShippingAddressResponse(orderDetails.shippingAddress()),
                toMoneyResponse(orderDetails.totalAmount()),
                orderDetails.items()
                        .stream()
                        .map(this::toOrderItemResponse)
                        .toList()
        );
    }

    public CreateOrderPaymentCommand toCreateOrderPaymentCommand(OrderPaymentRequest orderPaymentRequest) {
        if (orderPaymentRequest == null) {
            return new CreateOrderPaymentCommand(null);
        }
        return new CreateOrderPaymentCommand(orderPaymentRequest.getMethod());
    }

    public OrderPaymentResponse toOrderPaymentResponse(OrderPaymentDetails orderPaymentDetails) {
        return new OrderPaymentResponse(
                orderPaymentDetails.id(),
                orderPaymentDetails.orderId(),
                orderPaymentDetails.status(),
                orderPaymentDetails.method(),
                toMoneyResponse(orderPaymentDetails.amount()),
                orderPaymentDetails.createdAt()
        );
    }

    private ShippingAddressDetails toShippingAddressDetails(ShippingAddressRequest shippingAddressRequest) {
        if (shippingAddressRequest == null) {
            return null;
        }
        return new ShippingAddressDetails(
                shippingAddressRequest.getStreet(),
                shippingAddressRequest.getCity(),
                shippingAddressRequest.getPostalCode(),
                shippingAddressRequest.getCountry()
        );
    }

    private List<OrderItemDetails> toOrderItemDetails(List<OrderItemRequest> orderItemRequests) {
        if (orderItemRequests == null) {
            return Collections.emptyList();
        }
        return orderItemRequests.stream()
                .map(this::toOrderItemDetails)
                .toList();
    }

    private OrderItemDetails toOrderItemDetails(OrderItemRequest orderItemRequest) {
        return new OrderItemDetails(
                null,
                orderItemRequest.getProductName(),
                orderItemRequest.getQuantity(),
                toMoneyDetails(orderItemRequest.getPrice())
        );
    }

    private MoneyDetails toMoneyDetails(MoneyRequest moneyRequest) {
        if (moneyRequest == null) {
            return null;
        }
        return new MoneyDetails(moneyRequest.getAmount(), moneyRequest.getCurrency());
    }

    private ShippingAddressResponse toShippingAddressResponse(ShippingAddressDetails shippingAddressDetails) {
        return new ShippingAddressResponse(
                shippingAddressDetails.street(),
                shippingAddressDetails.city(),
                shippingAddressDetails.postalCode(),
                shippingAddressDetails.country()
        );
    }

    private MoneyResponse toMoneyResponse(MoneyDetails moneyDetails) {
        return new MoneyResponse(moneyDetails.amount(), moneyDetails.currency());
    }

    private OrderItemResponse toOrderItemResponse(OrderItemDetails orderItemDetails) {
        return new OrderItemResponse(
                orderItemDetails.id(),
                orderItemDetails.productName(),
                orderItemDetails.quantity(),
                toMoneyResponse(orderItemDetails.price())
        );
    }
}
