package ru.iprody.orderservice.integration.payment;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.wiremock.spring.ConfigureWireMock;
import org.wiremock.spring.EnableWireMock;
import ru.iprody.orderservice.application.payment.PaymentMethod;
import ru.iprody.orderservice.application.payment.PaymentStatus;
import ru.iprody.orderservice.integration.payment.dto.PaymentAmountRequest;
import ru.iprody.orderservice.integration.payment.dto.PaymentCreateRequest;
import ru.iprody.orderservice.integration.payment.dto.PaymentCreateResponse;

import java.math.BigDecimal;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("wiremock")
@EnableWireMock(
        @ConfigureWireMock(
                name = "payment-service",
                port = 9999,
                filesUnderClasspath = "wiremock"
        )
)
class PaymentClientIntegrationTest {

    @Autowired
    private PaymentClientAdapter paymentClientAdapter;

    @Test
    void createPayment_success() {
        PaymentCreateRequest request = new PaymentCreateRequest(
                1L,
                PaymentStatus.PENDING,
                PaymentMethod.CARD,
                new PaymentAmountRequest(new BigDecimal("100.00"), "RUB")
        );

        PaymentCreateResponse result = paymentClientAdapter.createPayment(request);

        assertNotNull(result);
        assertEquals(1L, result.id());
        assertEquals(1L, result.orderId());
        assertEquals(PaymentStatus.PENDING, result.status());
        assertEquals(PaymentMethod.CARD, result.method());
        verify(postRequestedFor(urlEqualTo("/api/payments"))
                .withHeader("X-Idempotency-Key", equalTo("1")));
    }

    @Test
    void createPayment_conflict_returnsCachedResponse() {
        // 409 Conflict означает: ключ идемпотентности уже существует,
        // payment-service возвращает тело с кешированным ответом.
        // PaymentClientAdapter.isAcceptable() = true для 409 → парсит и возвращает тело.
        PaymentCreateRequest request = new PaymentCreateRequest(
                409L,
                PaymentStatus.PENDING,
                PaymentMethod.CARD,
                new PaymentAmountRequest(new BigDecimal("100.00"), "RUB")
        );

        PaymentCreateResponse result = paymentClientAdapter.createPayment(request);

        assertNotNull(result);
        assertEquals(409L, result.orderId());
        verify(postRequestedFor(urlEqualTo("/api/payments"))
                .withHeader("X-Idempotency-Key", equalTo("409")));
    }

    @Test
    void createPayment_badRequest_throwsRuntimeException() {
        PaymentCreateRequest request = new PaymentCreateRequest(
                400L,
                PaymentStatus.PENDING,
                PaymentMethod.CARD,
                new PaymentAmountRequest(new BigDecimal("100.00"), "RUB")
        );

        assertThrows(RuntimeException.class, () -> paymentClientAdapter.createPayment(request));
        verify(postRequestedFor(urlEqualTo("/api/payments"))
                .withHeader("X-Idempotency-Key", equalTo("400")));
    }

    @Test
    void createPayment_serverError_throwsRuntimeException() {
        PaymentCreateRequest request = new PaymentCreateRequest(
                500L,
                PaymentStatus.PENDING,
                PaymentMethod.CARD,
                new PaymentAmountRequest(new BigDecimal("100.00"), "RUB")
        );

        assertThrows(RuntimeException.class, () -> paymentClientAdapter.createPayment(request));
        verify(postRequestedFor(urlEqualTo("/api/payments"))
                .withHeader("X-Idempotency-Key", equalTo("500")));
    }
}
