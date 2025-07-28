package com.orderfulfillment.paymentservice.client;

import reactor.core.publisher.Mono;

public interface OrderServiceClient {

    /**
     * Update order status when payment is completed
     */
    Mono<Void> updateOrderStatusToCompleted(Long orderId, Long paymentId);

    /**
     * Cancel order when payment fails
     */
    Mono<Void> cancelOrder(Long orderId, String reason);

    /**
     * Check if order service is available
     */
    Mono<Boolean> isOrderServiceAvailable();
}