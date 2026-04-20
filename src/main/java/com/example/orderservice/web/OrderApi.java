package com.example.orderservice.web;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import com.example.orderservice.web.dto.OrderPaymentRequest;
import com.example.orderservice.web.dto.OrderPaymentResponse;
import com.example.orderservice.web.dto.OrderRequest;
import com.example.orderservice.web.dto.OrderResponse;

@Tag(name = "Orders", description = "Операции с заказами и запуск оплаты заказа")
public interface OrderApi {

    @Operation(summary = "Создать заказ")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Заказ создан"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос")
    })
    @ResponseStatus(HttpStatus.CREATED)
    OrderResponse create(@RequestBody OrderRequest orderRequest);

    @Operation(summary = "Получить список заказов")
    @ApiResponse(responseCode = "200", description = "Список заказов получен")
    List<OrderResponse> getAll();

    @Operation(summary = "Получить заказ по идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заказ найден"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    OrderResponse getById(@PathVariable("id") Long orderId);

    @Operation(summary = "Обновить заказ")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Заказ обновлён"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    OrderResponse update(@PathVariable("id") Long orderId, @RequestBody OrderRequest orderRequest);

    @Operation(summary = "Удалить заказ")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Заказ удалён"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void delete(@PathVariable("id") Long orderId);

    @Operation(summary = "Отправить запрос на оплату заказа")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Платёж создан в payment-service"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден"),
            @ApiResponse(responseCode = "502", description = "Ошибка вызова payment-service")
    })
    @ResponseStatus(HttpStatus.CREATED)
    OrderPaymentResponse createPayment(@PathVariable("id") Long orderId,
                                       @RequestBody OrderPaymentRequest orderPaymentRequest);

    @Operation(summary = "Отправить асинхронный запрос на оплату через RabbitMQ")
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Запрос принят, платёж обрабатывается асинхронно"),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    @ResponseStatus(HttpStatus.ACCEPTED)
    OrderResponse requestPaymentAsync(@PathVariable("id") Long orderId,
                                      @RequestBody OrderPaymentRequest orderPaymentRequest);
}
