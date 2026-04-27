package ru.iprody.orderservice.application.command;

import java.util.List;

import ru.iprody.orderservice.application.dto.OrderItemDetails;
import ru.iprody.orderservice.application.dto.ShippingAddressDetails;
import ru.iprody.orderservice.domain.model.OrderStatus;

public record OrderCommand(
        Long customerId,
        OrderStatus status,
        ShippingAddressDetails shippingAddress,
        List<OrderItemDetails> items
) {
}
