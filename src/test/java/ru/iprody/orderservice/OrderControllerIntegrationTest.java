package ru.iprody.orderservice;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.iprody.orderservice.application.payment.PaymentMethod;
import ru.iprody.orderservice.application.payment.PaymentStatus;
import ru.iprody.orderservice.domain.model.OrderStatus;
import ru.iprody.orderservice.domain.repository.OrderRepository;
import ru.iprody.orderservice.integration.payment.PaymentServiceClient;
import ru.iprody.orderservice.integration.payment.dto.PaymentAmountResponse;
import ru.iprody.orderservice.integration.payment.dto.PaymentCreateResponse;
import ru.iprody.orderservice.web.dto.MoneyRequest;
import ru.iprody.orderservice.web.dto.OrderPaymentRequest;
import ru.iprody.orderservice.web.dto.OrderItemRequest;
import ru.iprody.orderservice.web.dto.OrderRequest;
import ru.iprody.orderservice.web.dto.ShippingAddressRequest;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @MockitoBean
    private PaymentServiceClient paymentServiceClient;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void shouldPerformCrudForOrder() throws Exception {
        OrderRequest createRequest = new OrderRequest();
        createRequest.setCustomerId(101L);
        createRequest.setStatus(OrderStatus.NEW);
        createRequest.setShippingAddress(new ShippingAddressRequest("Tverskaya 1", "Moscow", "125009", "RU"));
        createRequest.setItems(List.of(
                new OrderItemRequest("Notebook", 1, new MoneyRequest(new BigDecimal("79990.00"), "RUB")),
                new OrderItemRequest("Mouse", 2, new MoneyRequest(new BigDecimal("1990.00"), "RUB"))
        ));

        MvcResult createResult = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.customerId").value(101))
                .andExpect(jsonPath("$.status").value("NEW"))
                .andExpect(jsonPath("$.totalAmount.amount").value(83970.00))
                .andReturn();

        JsonNode createdOrder = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long orderId = createdOrder.get("id").asLong();

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(orderId));

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.items", hasSize(2)));

        OrderRequest updateRequest = new OrderRequest();
        updateRequest.setCustomerId(101L);
        updateRequest.setStatus(OrderStatus.CONFIRMED);
        updateRequest.setShippingAddress(new ShippingAddressRequest("Lenina 10", "Moscow", "101000", "RU"));
        updateRequest.setItems(List.of(
                new OrderItemRequest("Notebook", 1, new MoneyRequest(new BigDecimal("79990.00"), "RUB"))
        ));

        mockMvc.perform(put("/api/orders/{id}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.shippingAddress.street").value("Lenina 10"))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.totalAmount.amount").value(79990.00));

        mockMvc.perform(delete("/api/orders/{id}", orderId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/orders/{id}", orderId))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCreatePaymentForOrderViaFeign() throws Exception {
        OrderRequest createOrderRequest = new OrderRequest();
        createOrderRequest.setCustomerId(101L);
        createOrderRequest.setStatus(OrderStatus.NEW);
        createOrderRequest.setShippingAddress(new ShippingAddressRequest("Tverskaya 1", "Moscow", "125009", "RU"));
        createOrderRequest.setItems(List.of(
                new OrderItemRequest("Notebook", 1, new MoneyRequest(new BigDecimal("79990.00"), "RUB")),
                new OrderItemRequest("Mouse", 2, new MoneyRequest(new BigDecimal("1990.00"), "RUB"))
        ));

        MvcResult createOrderResult = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createOrderRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        long orderId = objectMapper.readTree(createOrderResult.getResponse().getContentAsString()).get("id").asLong();

        given(paymentServiceClient.createPayment(
                any(String.class),
                argThat(request ->
                        request.orderId().equals(orderId)
                                && request.status() == PaymentStatus.PENDING
                                && request.method() == PaymentMethod.CARD
                                && request.amount().amount().compareTo(new BigDecimal("83970.00")) == 0
                                && "RUB".equals(request.amount().currency())
                ))).willReturn(new PaymentCreateResponse(
                1L,
                orderId,
                PaymentStatus.PENDING,
                PaymentMethod.CARD,
                new PaymentAmountResponse(new BigDecimal("83970.00"), "RUB"),
                LocalDateTime.of(2026, 3, 17, 10, 0)
        ));

        OrderPaymentRequest paymentRequest = new OrderPaymentRequest();
        paymentRequest.setMethod(PaymentMethod.CARD);

        mockMvc.perform(post("/api/orders/{id}/payment", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderId").value(orderId))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.method").value("CARD"))
                .andExpect(jsonPath("$.amount.amount").value(83970.00));

        verify(paymentServiceClient).createPayment(
                any(String.class),
                argThat(request ->
                        request.orderId().equals(orderId)
                                && request.status() == PaymentStatus.PENDING
                                && request.method() == PaymentMethod.CARD
                                && request.amount().amount().compareTo(new BigDecimal("83970.00")) == 0
                                && "RUB".equals(request.amount().currency())
                ));
    }
}
