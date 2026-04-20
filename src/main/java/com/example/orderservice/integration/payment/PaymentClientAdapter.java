package com.example.orderservice.integration.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import com.example.orderservice.integration.payment.dto.PaymentCreateRequest;
import com.example.orderservice.integration.payment.dto.PaymentCreateResponse;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentClientAdapter {

    private final PaymentServiceClient paymentServiceClient;
    private final ObjectMapper objectMapper;

    @Retry(name = "paymentServiceRetry")
    @CircuitBreaker(name = "paymentServiceCircuitBreaker")
    @RateLimiter(name = "paymentClientRateLimiter")
    @Bulkhead(name = "paymentClientBulkhead")
    public PaymentCreateResponse createPayment(PaymentCreateRequest request) {
        String idempotencyKey = request.orderId().toString();
        log.debug("Calling payment-service, idempotency-key={}", idempotencyKey);
        try {
            return paymentServiceClient.createPayment(idempotencyKey, request);
        } catch (FeignException ex) {
            return processException(ex);
        }
    }

    private PaymentCreateResponse processException(FeignException ex) {
        HttpStatusCode statusCode = HttpStatusCode.valueOf(ex.status());
        Optional<ByteBuffer> bodyOptional = ex.responseBody();

        if (isAcceptable(statusCode) && bodyOptional.isPresent()) {
            return getResponse(bodyOptional.get());
        } else {
            throw new RuntimeException("Payment service request failed: " + ex.getMessage(), ex);
        }
    }

    private boolean isAcceptable(HttpStatusCode statusCode) {
        return statusCode.is2xxSuccessful() || statusCode.isSameCodeAs(HttpStatus.CONFLICT);
    }

    private PaymentCreateResponse getResponse(ByteBuffer body) {
        try {
            return objectMapper.readValue(body.array(), PaymentCreateResponse.class);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse payment service response", e);
        }
    }
}
