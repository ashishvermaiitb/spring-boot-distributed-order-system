package com.orderfulfillment.orderservice.client.impl;

import com.orderfulfillment.orderservice.client.CustomerServiceClient;
import com.orderfulfillment.orderservice.dto.CustomerDto;
import com.orderfulfillment.orderservice.exception.CustomerNotFoundException;
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
public class CustomerServiceClientImpl implements CustomerServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceClientImpl.class);

    private final WebClient webClient;

    @Value("${external-services.customer-service.timeout:5000}")
    private int timeout;

    public CustomerServiceClientImpl(WebClient.Builder webClientBuilder,
                                     @Value("${external-services.customer-service.base-url}") String baseUrl) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    @Override
    @CircuitBreaker(name = "customer-service", fallbackMethod = "getCustomerByIdFallback")
    @Retry(name = "customer-service")
    public Mono<CustomerDto> getCustomerById(Long customerId) {
        logger.info("Fetching customer with ID: {}", customerId);

        return webClient.get()
                .uri("/api/v1/customers/{customerId}", customerId)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(), response ->
                        Mono.error(new CustomerNotFoundException("Customer not found with ID: " + customerId)))
                .onStatus(status -> status.is5xxServerError(), response -> {
                    logger.error("Error fetching customer: {}", response.statusCode());
                    return Mono.error(new RuntimeException("Customer service error: " + response.statusCode()));
                })
                .bodyToMono(CustomerDto.class)
                .timeout(Duration.ofMillis(timeout))
                .doOnSuccess(customer -> logger.info("Successfully fetched customer: {}", customerId))
                .doOnError(error -> logger.error("Failed to fetch customer: {}", customerId, error));
    }

    @Override
    @CircuitBreaker(name = "customer-service", fallbackMethod = "validateCustomerFallback")
    @Retry(name = "customer-service")
    public Mono<Boolean> validateCustomer(Long customerId) {
        logger.info("Validating customer with ID: {}", customerId);

        return webClient.get()
                .uri("/api/v1/customers/{customerId}/exists", customerId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .timeout(Duration.ofMillis(timeout))
                .doOnSuccess(exists -> logger.info("Customer {} validation result: {}", customerId, exists))
                .doOnError(error -> logger.error("Failed to validate customer: {}", customerId, error));
    }

    // Fallback methods
    public Mono<CustomerDto> getCustomerByIdFallback(Long customerId, Exception ex) {
        logger.warn("Fallback: Unable to fetch customer {}, error: {}", customerId, ex.getMessage());
        return Mono.error(new CustomerNotFoundException("Customer service unavailable"));
    }

    public Mono<Boolean> validateCustomerFallback(Long customerId, Exception ex) {
        logger.warn("Fallback: Unable to validate customer {}, error: {}", customerId, ex.getMessage());
        return Mono.just(false);
    }
}