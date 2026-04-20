package com.example.orderservice.web;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import com.example.orderservice.application.OrderApplicationService;
import com.example.orderservice.web.dto.OrderPaymentRequest;
import com.example.orderservice.web.dto.OrderPaymentResponse;
import com.example.orderservice.web.dto.OrderRequest;
import com.example.orderservice.web.dto.OrderResponse;
import com.example.orderservice.web.mapper.OrderWebMapper;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController implements OrderApi {

    private final OrderApplicationService orderApplicationService;
    private final OrderWebMapper orderWebMapper;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse create(@RequestBody OrderRequest orderRequest) {
        return orderWebMapper.toOrderResponse(
                orderApplicationService.create(orderWebMapper.toOrderCommand(orderRequest))
        );
    }

    @Override
    @GetMapping
    public List<OrderResponse> getAll() {
        return orderApplicationService.getAll()
                .stream()
                .map(orderWebMapper::toOrderResponse)
                .toList();
    }

    @Override
    @GetMapping("/{id}")
    public OrderResponse getById(@PathVariable("id") Long orderId) {
        return orderWebMapper.toOrderResponse(orderApplicationService.getById(orderId));
    }

    @Override
    @PutMapping("/{id}")
    public OrderResponse update(@PathVariable("id") Long orderId, @RequestBody OrderRequest orderRequest) {
        return orderWebMapper.toOrderResponse(
                orderApplicationService.update(orderId, orderWebMapper.toOrderCommand(orderRequest))
        );
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long orderId) {
        orderApplicationService.delete(orderId);
    }

    @Override
    @PostMapping("/{id}/payment")
    @ResponseStatus(HttpStatus.CREATED)
    public OrderPaymentResponse createPayment(
            @PathVariable("id") Long orderId,
            @RequestBody OrderPaymentRequest orderPaymentRequest
    ) {
        return orderWebMapper.toOrderPaymentResponse(
                orderApplicationService.createPayment(
                        orderId,
                        orderWebMapper.toCreateOrderPaymentCommand(orderPaymentRequest)
                )
        );
    }

    @Override
    @PostMapping("/{id}/payment/async")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public OrderResponse requestPaymentAsync(
            @PathVariable("id") Long orderId,
            @RequestBody OrderPaymentRequest orderPaymentRequest
    ) {
        return orderWebMapper.toOrderResponse(
                orderApplicationService.requestPaymentAsync(
                        orderId,
                        orderWebMapper.toCreateOrderPaymentCommand(orderPaymentRequest)
                )
        );
    }
}
