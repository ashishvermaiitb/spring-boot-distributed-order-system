package com.orderfulfillment.orderservice.client.impl;

import com.orderfulfillment.orderservice.client.PaymentServiceClient;
import com.orderfulfillment.orderservice.dto.PaymentDto;
import com.orderfulfillment.orderservice.dto.PaymentRequestDto;
import com.orderfulfillment.orderservice.exception.PaymentServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
public class PaymentServiceClientImpl implements PaymentServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceClientImpl.class);

    private final WebClient webClient;

    @Value("${external-services.payment-service.timeout:5000}")
    private int timeout;

    public PaymentServiceClientImpl(WebClient.Builder webClientBuilder,
                                    @Value("${external-services.payment-service.base-url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    @Override
    @CircuitBreaker(name = "payment-service", fallbackMethod = "createPaymentFallback")
    @Retry(name = "payment-service")
    public Mono<PaymentDto> createPayment(PaymentRequestDto paymentRequestDto) {
        logger.info("Creating payment for order: {}", paymentRequestDto.getOrderId());

        return webClient.post()
                .uri("/api/v1/payments")
                .bodyValue(paymentRequestDto)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> {
                    logger.error("Error creating payment: {}", response.statusCode());
                    return Mono.error(new PaymentServiceException("Payment creation failed: " + response.statusCode()));
                })
                .onStatus(status -> status.is5xxServerError(), response -> {
                    logger.error("Error creating payment: {}", response.statusCode());
                    return Mono.error(new PaymentServiceException("Payment creation failed: " + response.statusCode()));
                })
                .bodyToMono(PaymentDto.class)
                .timeout(Duration.ofMillis(timeout))
                .doOnSuccess(payment -> logger.info("Successfully created payment: {}", payment.getId()))
                .doOnError(error -> logger.error("Failed to create payment for order: {}",
                        paymentRequestDto.getOrderId(), error));
    }

    @Override
    @CircuitBreaker(name = "payment-service", fallbackMethod = "getPaymentByOrderIdFallback")
    @Retry(name = "payment-service")
    public Mono<PaymentDto> getPaymentByOrderId(Long orderId) {
        logger.info("Fetching payment for order: {}", orderId);

        return webClient.get()
                .uri("/api/v1/payments/order/{orderId}", orderId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response -> {
                    logger.error("Error fetching payment: {}", response.statusCode());
                    return Mono.error(new PaymentServiceException("Payment fetch failed: " + response.statusCode()));
                })
                .onStatus(status -> status.is5xxServerError(), response -> {
                    logger.error("Error fetching payment: {}", response.statusCode());
                    return Mono.error(new PaymentServiceException("Payment fetch failed: " + response.statusCode()));
                })
                .bodyToMono(PaymentDto.class)
                .timeout(Duration.ofMillis(timeout))
                .doOnSuccess(payment -> logger.info("Successfully fetched payment: {}", payment.getId()))
                .doOnError(error -> logger.error("Failed to fetch payment for order: {}", orderId, error));
    }

    // Fallback methods
    public Mono<PaymentDto> createPaymentFallback(PaymentRequestDto paymentRequestDto, Exception ex) {
        logger.warn("Fallback: Unable to create payment for order {}, error: {}",
                paymentRequestDto.getOrderId(), ex.getMessage());
        return Mono.error(new PaymentServiceException("Payment service unavailable"));
    }

    public Mono<PaymentDto> getPaymentByOrderIdFallback(Long orderId, Exception ex) {
        logger.warn("Fallback: Unable to fetch payment for order {}, error: {}", orderId, ex.getMessage());
        return Mono.error(new PaymentServiceException("Payment service unavailable"));
    }
}