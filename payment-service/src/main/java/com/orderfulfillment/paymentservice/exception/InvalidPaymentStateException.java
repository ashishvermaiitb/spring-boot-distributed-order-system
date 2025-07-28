package com.orderfulfillment.paymentservice.exception;

public class InvalidPaymentStateException extends RuntimeException {

    public InvalidPaymentStateException(String message) {
        super(message);
    }

    public InvalidPaymentStateException(String message, Throwable cause) {
        super(message, cause);
    }
}