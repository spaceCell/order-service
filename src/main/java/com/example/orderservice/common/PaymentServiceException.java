package com.example.orderservice.common;

public class PaymentServiceException extends RuntimeException {

    public PaymentServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
