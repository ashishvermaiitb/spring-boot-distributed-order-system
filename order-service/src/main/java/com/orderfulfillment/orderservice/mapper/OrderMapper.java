package com.orderfulfillment.orderservice.mapper;

import com.orderfulfillment.orderservice.dto.*;
import com.orderfulfillment.orderservice.entity.Order;
import com.orderfulfillment.orderservice.entity.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    public Order toEntity(OrderRequestDto orderRequestDto) {
        if (orderRequestDto == null) return null;

        Order order = new Order(orderRequestDto.getCustomerId());
        order.setNotes(orderRequestDto.getNotes());

        if (orderRequestDto.getOrderItems() != null) {
            List<OrderItem> orderItems = orderRequestDto.getOrderItems().stream()
                    .map(this::toEntity)
                    .collect(Collectors.toList());

            orderItems.forEach(order::addOrderItem);
        }

        return order;
    }

    public OrderItem toEntity(OrderItemDto orderItemDto) {
        if (orderItemDto == null) return null;

        OrderItem orderItem = new OrderItem(
                orderItemDto.getProductName(),
                orderItemDto.getQuantity(),
                orderItemDto.getUnitPrice()
        );
        orderItem.setProductDescription(orderItemDto.getProductDescription());
        orderItem.setProductCategory(orderItemDto.getProductCategory());

        return orderItem;
    }

    public OrderResponseDto toResponseDto(Order order) {
        if (order == null) return null;

        OrderResponseDto responseDto = new OrderResponseDto();
        responseDto.setId(order.getId());
        responseDto.setCustomerId(order.getCustomerId());
        responseDto.setPaymentId(order.getPaymentId());
        responseDto.setStatus(order.getStatus());
        responseDto.setTotalAmount(order.getTotalAmount());
        responseDto.setNotes(order.getNotes());
        responseDto.setFailureReason(order.getFailureReason());
        responseDto.setCreatedAt(order.getCreatedAt());
        responseDto.setUpdatedAt(order.getUpdatedAt());

        if (order.getOrderItems() != null) {
            List<OrderItemResponseDto> orderItemDtos = order.getOrderItems().stream()
                    .map(this::toResponseDto)
                    .collect(Collectors.toList());
            responseDto.setOrderItems(orderItemDtos);
        }

        return responseDto;
    }

    public OrderItemResponseDto toResponseDto(OrderItem orderItem) {
        if (orderItem == null) return null;

        OrderItemResponseDto responseDto = new OrderItemResponseDto();
        responseDto.setId(orderItem.getId());
        responseDto.setProductName(orderItem.getProductName());
        responseDto.setProductDescription(orderItem.getProductDescription());
        responseDto.setProductCategory(orderItem.getProductCategory());
        responseDto.setQuantity(orderItem.getQuantity());
        responseDto.setUnitPrice(orderItem.getUnitPrice());
        responseDto.setTotalPrice(orderItem.getTotalPrice());

        return responseDto;
    }
}