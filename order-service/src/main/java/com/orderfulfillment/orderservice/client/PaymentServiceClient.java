package com.orderfulfillment.orderservice.client;

import com.orderfulfillment.orderservice.dto.PaymentDto;
import com.orderfulfillment.orderservice.dto.PaymentRequestDto;
import reactor.core.publisher.Mono;

public interface PaymentServiceClient {
    Mono<PaymentDto> createPayment(PaymentRequestDto paymentRequestDto);
    Mono<PaymentDto> getPaymentByOrderId(Long orderId);
}