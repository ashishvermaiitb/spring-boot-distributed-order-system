package com.orderfulfillment.orderservice.dto;

import java.math.BigDecimal;

public class PaymentRequestDto {

    private Long orderId;
    private BigDecimal amount;
    private String paymentMethod;

    // Constructors
    public PaymentRequestDto() {}

    public PaymentRequestDto(Long orderId, BigDecimal amount, String paymentMethod) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
    }

    // Getters and Setters
    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
}