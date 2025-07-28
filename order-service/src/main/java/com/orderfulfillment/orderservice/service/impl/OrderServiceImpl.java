package com.orderfulfillment.orderservice.service.impl;

import com.orderfulfillment.orderservice.client.CustomerServiceClient;
import com.orderfulfillment.orderservice.client.PaymentServiceClient;
import com.orderfulfillment.orderservice.dto.*;
        import com.orderfulfillment.orderservice.entity.Order;
import com.orderfulfillment.orderservice.enums.OrderStatus;
import com.orderfulfillment.orderservice.exception.*;
        import com.orderfulfillment.orderservice.mapper.OrderMapper;
import com.orderfulfillment.orderservice.repository.OrderRepository;
import com.orderfulfillment.orderservice.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final CustomerServiceClient customerServiceClient;
    private final PaymentServiceClient paymentServiceClient;

    // Circuit breaker state tracking
    private final AtomicInteger paymentServiceFailureCount = new AtomicInteger(0);
    private volatile boolean paymentServiceCircuitOpen = false;
    private volatile long circuitOpenTime = 0;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderMapper orderMapper,
                            CustomerServiceClient customerServiceClient,
                            PaymentServiceClient paymentServiceClient) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
        this.customerServiceClient = customerServiceClient;
        this.paymentServiceClient = paymentServiceClient;
    }

    @Override
    public OrderResponseDto createOrder(OrderRequestDto orderRequestDto) {
        logger.info("Creating order for customer ID: {}", orderRequestDto.getCustomerId());

        try {
            // Step 1: Validate customer exists
            validateCustomer(orderRequestDto.getCustomerId());

            // Step 2: Create and save order
            Order order = orderMapper.toEntity(orderRequestDto);
            Order savedOrder = orderRepository.save(order);
            logger.info("Order created with ID: {}", savedOrder.getId());

            // Step 3: Confirm order
            savedOrder.confirm();
            savedOrder = orderRepository.save(savedOrder);

            // Step 4: Check payment service circuit breaker
            if (isPaymentServiceAvailable()) {
                // Step 5: Initiate payment
                initiatePayment(savedOrder);
            } else {
                logger.warn("Payment service circuit is open, failing order immediately");
                savedOrder.fail("Payment service is unavailable");
                savedOrder = orderRepository.save(savedOrder);
            }

            return orderMapper.toResponseDto(savedOrder);

        } catch (CustomerNotFoundException e) {
            logger.error("Customer validation failed for ID: {}", orderRequestDto.getCustomerId());
            throw e;
        } catch (Exception e) {
            logger.error("Error creating order for customer: {}", orderRequestDto.getCustomerId(), e);
            throw new OrderProcessingException("Failed to create order: " + e.getMessage(), e);
        }
    }

    private void validateCustomer(Long customerId) {
        Boolean customerExists = customerServiceClient.validateCustomer(customerId)
                .timeout(Duration.ofSeconds(5))
                .block();

        if (!Boolean.TRUE.equals(customerExists)) {
            throw new CustomerNotFoundException("Customer not found with ID: " + customerId);
        }

        logger.info("Customer validation successful for ID: {}", customerId);
    }

    private void initiatePayment(Order order) {
        try {
            order.markPaymentProcessing();
            orderRepository.save(order);

            PaymentRequestDto paymentRequest = new PaymentRequestDto(
                    order.getId(),
                    order.getTotalAmount(),
                    "CREDIT_CARD" // Default payment method
            );

            PaymentDto payment = paymentServiceClient.createPayment(paymentRequest)
                    .timeout(Duration.ofSeconds(10))
                    .block();

            if (payment != null) {
                logger.info("Payment initiated successfully for order: {} with payment ID: {}",
                        order.getId(), payment.getId());
                resetPaymentServiceFailureCount();
            } else {
                throw new PaymentServiceException("Payment creation returned null");
            }

        } catch (Exception e) {
            logger.error("Payment initiation failed for order: {}", order.getId(), e);
            incrementPaymentServiceFailureCount();

            order.fail("Payment initiation failed: " + e.getMessage());
            orderRepository.save(order);

            throw new PaymentServiceException("Payment initiation failed", e);
        }
    }

    private boolean isPaymentServiceAvailable() {
        if (!paymentServiceCircuitOpen) {
            return true;
        }

        // Check if circuit should be closed (5 minutes timeout)
        if (System.currentTimeMillis() - circuitOpenTime > 300000) {
            paymentServiceCircuitOpen = false;
            paymentServiceFailureCount.set(0);
            logger.info("Payment service circuit breaker reset");
            return true;
        }

        return false;
    }

    private void incrementPaymentServiceFailureCount() {
        int failures = paymentServiceFailureCount.incrementAndGet();
        logger.warn("Payment service failure count: {}", failures);

        if (failures >= 5) {
            paymentServiceCircuitOpen = true;
            circuitOpenTime = System.currentTimeMillis();
            logger.error("Payment service circuit breaker opened due to {} failures", failures);
        }
    }

    private void resetPaymentServiceFailureCount() {
        paymentServiceFailureCount.set(0);
        paymentServiceCircuitOpen = false;
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponseDto getOrderById(Long orderId) {
        logger.info("Fetching order with ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    logger.error("Order not found with ID: {}", orderId);
                    return new OrderNotFoundException("Order not found with ID: " + orderId);
                });

        return orderMapper.toResponseDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getAllOrders() {
        logger.info("Fetching all orders");

        List<Order> orders = orderRepository.findAll();
        logger.info("Found {} orders", orders.size());

        return orders.stream()
                .map(orderMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByCustomerId(Long customerId) {
        logger.info("Fetching orders for customer ID: {}", customerId);

        List<Order> orders = orderRepository.findByCustomerId(customerId);
        logger.info("Found {} orders for customer: {}", orders.size(), customerId);

        return orders.stream()
                .map(orderMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByStatus(OrderStatus status) {
        logger.info("Fetching orders with status: {}", status);

        List<Order> orders = orderRepository.findByStatus(status);
        logger.info("Found {} orders with status: {}", orders.size(), status);

        return orders.stream()
                .map(orderMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CompleteOrderDetailsDto getCompleteOrderDetails(Long orderId) {
        logger.info("Fetching complete order details for order ID: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        OrderResponseDto orderDto = orderMapper.toResponseDto(order);

        try {
            // Fetch customer details
            CustomerDto customer = customerServiceClient.getCustomerById(order.getCustomerId())
                    .timeout(Duration.ofSeconds(5))
                    .onErrorReturn(new CustomerDto()) // Return empty DTO on error
                    .block();

            // Fetch payment details if payment exists
            PaymentDto payment = null;
            if (order.getPaymentId() != null || order.getStatus() != OrderStatus.PENDING) {
                payment = paymentServiceClient.getPaymentByOrderId(order.getId())
                        .timeout(Duration.ofSeconds(5))
                        .onErrorReturn(new PaymentDto()) // Return empty DTO on error
                        .block();
            }

            CompleteOrderDetailsDto completeDetails = new CompleteOrderDetailsDto(orderDto, customer, payment);
            logger.info("Successfully fetched complete order details for order: {}", orderId);

            return completeDetails;

        } catch (Exception e) {
            logger.error("Error fetching complete order details for order: {}", orderId, e);
            // Return partial data - at least the order details
            return new CompleteOrderDetailsDto(orderDto, new CustomerDto(), new PaymentDto());
        }
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus status, Long paymentId) {
        logger.info("Updating order {} status to {} with payment ID: {}", orderId, status, paymentId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        try {
            if (status == OrderStatus.COMPLETED) {
                order.complete(paymentId);
            } else {
                order.setStatus(status);
                if (paymentId != null) {
                    order.setPaymentId(paymentId);
                }
            }

            orderRepository.save(order);
            logger.info("Order {} status updated successfully to {}", orderId, status);

        } catch (IllegalStateException e) {
            logger.error("Invalid state transition for order {}: {}", orderId, e.getMessage());
            throw new InvalidOrderStateException("Invalid state transition: " + e.getMessage());
        }
    }

    @Override
    public void cancelOrder(Long orderId, String reason) {
        logger.info("Cancelling order {} with reason: {}", orderId, reason);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with ID: " + orderId));

        try {
            order.cancel(reason);
            orderRepository.save(order);
            logger.info("Order {} cancelled successfully", orderId);

        } catch (IllegalStateException e) {
            logger.error("Cannot cancel order {}: {}", orderId, e.getMessage());
            throw new InvalidOrderStateException("Cannot cancel order: " + e.getMessage());
        }
    }
}