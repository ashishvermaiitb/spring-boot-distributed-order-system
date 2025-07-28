package com.orderfulfillment.orderservice.client;

import com.orderfulfillment.orderservice.dto.CustomerDto;
import reactor.core.publisher.Mono;

public interface CustomerServiceClient {
    Mono<CustomerDto> getCustomerById(Long customerId);
    Mono<Boolean> validateCustomer(Long customerId);
}