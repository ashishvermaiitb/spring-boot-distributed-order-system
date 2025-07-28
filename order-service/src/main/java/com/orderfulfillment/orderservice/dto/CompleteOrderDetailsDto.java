package com.orderfulfillment.orderservice.dto;

public class CompleteOrderDetailsDto {

    private OrderResponseDto order;
    private CustomerDto customer;
    private PaymentDto payment;

    // Constructors
    public CompleteOrderDetailsDto() {}

    public CompleteOrderDetailsDto(OrderResponseDto order, CustomerDto customer, PaymentDto payment) {
        this.order = order;
        this.customer = customer;
        this.payment = payment;
    }

    // Getters and Setters
    public OrderResponseDto getOrder() { return order; }
    public void setOrder(OrderResponseDto order) { this.order = order; }

    public CustomerDto getCustomer() { return customer; }
    public void setCustomer(CustomerDto customer) { this.customer = customer; }

    public PaymentDto getPayment() { return payment; }
    public void setPayment(PaymentDto payment) { this.payment = payment; }
}