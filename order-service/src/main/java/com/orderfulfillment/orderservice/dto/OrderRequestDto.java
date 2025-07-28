package com.orderfulfillment.orderservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class OrderRequestDto {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotEmpty(message = "Order items are required")
    @Valid
    private List<OrderItemDto> orderItems;

    private String notes;

    // Constructors
    public OrderRequestDto() {}

    public OrderRequestDto(Long customerId, List<OrderItemDto> orderItems) {
        this.customerId = customerId;
        this.orderItems = orderItems;
    }

    // Getters and Setters
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public List<OrderItemDto> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemDto> orderItems) { this.orderItems = orderItems; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}