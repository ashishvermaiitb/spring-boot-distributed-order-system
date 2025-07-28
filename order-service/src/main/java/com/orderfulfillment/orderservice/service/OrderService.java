package com.orderfulfillment.orderservice.service;

import com.orderfulfillment.orderservice.dto.CompleteOrderDetailsDto;
import com.orderfulfillment.orderservice.dto.OrderRequestDto;
import com.orderfulfillment.orderservice.dto.OrderResponseDto;
import com.orderfulfillment.orderservice.enums.OrderStatus;

import java.util.List;

public interface OrderService {

    OrderResponseDto createOrder(OrderRequestDto orderRequestDto);

    OrderResponseDto getOrderById(Long orderId);

    List<OrderResponseDto> getAllOrders();

    List<OrderResponseDto> getOrdersByCustomerId(Long customerId);

    List<OrderResponseDto> getOrdersByStatus(OrderStatus status);

    CompleteOrderDetailsDto getCompleteOrderDetails(Long orderId);

    void updateOrderStatus(Long orderId, OrderStatus status, Long paymentId);

    void cancelOrder(Long orderId, String reason);
}