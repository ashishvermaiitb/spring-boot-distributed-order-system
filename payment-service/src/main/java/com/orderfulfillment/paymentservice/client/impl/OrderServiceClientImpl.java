package com.orderfulfillment.paymentservice.client.impl;

import com.orderfulfillment.paymentservice.client.OrderServiceClient;
import com.orderfulfillment.paymentservice.exception.OrderServiceException;
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
import java.util.Map;

@Component
public class OrderServiceClientImpl implements OrderServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceClientImpl.class);

    private final WebClient webClient;

    @Value("${external-services.order-service.timeout:5000}")
    private int timeout;

    public OrderServiceClientImpl(WebClient.Builder webClientBuilder,
                                  @Value("${external-services.order-service.base-url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    @Override
    @CircuitBreaker(name = "order-service", fallbackMethod = "updateOrderStatusFallback")
    @Retry(name = "order-service")
    public Mono<Void> updateOrderStatusToCompleted(Long orderId, Long paymentId) {
        logger.info("Updating order {} status to completed with payment {}", orderId, paymentId);

        Map<String, Object> requestBody = Map.of(
                "status", "COMPLETED",
                "paymentId", paymentId
        );

        return webClient.put()
                .uri("/api/v1/orders/{orderId}/status", orderId)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.isError(), response -> {
                    logger.error("Error updating order status: {}", response.statusCode());
                    return Mono.error(new OrderServiceException(
                            "Failed to update order status: " + response.statusCode()));
                })
                .toEntity(Void.class)
                .timeout(Duration.ofMillis(timeout))
                .doOnSuccess(response -> logger.info("Successfully updated order {} status", orderId))
                .doOnError(error -> logger.error("Failed to update order {} status", orderId, error))
                .then();
    }

    @Override
    @CircuitBreaker(name = "order-service", fallbackMethod = "cancelOrderFallback")
    @Retry(name = "order-service")
    public Mono<Void> cancelOrder(Long orderId, String reason) {
        logger.info("Cancelling order {} with reason: {}", orderId, reason);

        Map<String, Object> requestBody = Map.of(
                "status", "CANCELLED",
                "reason", reason
        );

        return webClient.put()
                .uri("/api/v1/orders/{orderId}/cancel", orderId)
                .bodyValue(requestBody)
                .retrieve()
                .onStatus(status -> status.isError(), response -> {
                    logger.error("Error cancelling order: {}", response.statusCode());
                    return Mono.error(new OrderServiceException(
                            "Failed to cancel order: " + response.statusCode()));
                })
                .toEntity(Void.class)
                .timeout(Duration.ofMillis(timeout))
                .doOnSuccess(response -> logger.info("Successfully cancelled order {}", orderId))
                .doOnError(error -> logger.error("Failed to cancel order {}", orderId, error))
                .then();
    }

    @Override
    public Mono<Boolean> isOrderServiceAvailable() {
        return webClient.get()
                .uri("/api/v1/orders/health")
                .retrieve()
                .toEntity(String.class)
                .timeout(Duration.ofMillis(timeout))
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .doOnSuccess(available -> logger.debug("Order service available: {}", available))
                .onErrorReturn(false);
    }

    // Circuit breaker fallback methods
    public Mono<Void> updateOrderStatusFallback(Long orderId, Long paymentId, Exception ex) {
        logger.warn("Fallback: Failed to update order {} status, will retry later. Error: {}",
                orderId, ex.getMessage());
        // In production, this could queue the request for later retry
        return Mono.empty();
    }

    public Mono<Void> cancelOrderFallback(Long orderId, String reason, Exception ex) {
        logger.warn("Fallback: Failed to cancel order {}, will retry later. Error: {}",
                orderId, ex.getMessage());
        // In production, this could queue the request for later retry
        return Mono.empty();
    }
}