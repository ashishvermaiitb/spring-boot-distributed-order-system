package com.orderfulfillment.orderservice.entity;

import com.orderfulfillment.orderservice.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Customer ID is required")
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "payment_id")
    private Long paymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(name = "notes")
    private String notes;

    @Column(name = "failure_reason")
    private String failureReason;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public Order() {}

    public Order(Long customerId) {
        this.customerId = customerId;
        this.status = OrderStatus.PENDING;
    }

    // Business methods
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
        calculateTotalAmount();
    }

    public void removeOrderItem(OrderItem orderItem) {
        orderItems.remove(orderItem);
        orderItem.setOrder(null);
        calculateTotalAmount();
    }

    public void calculateTotalAmount() {
        this.totalAmount = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void confirm() {
        if (status.canTransitionTo(OrderStatus.CONFIRMED)) {
            this.status = OrderStatus.CONFIRMED;
        } else {
            throw new IllegalStateException("Cannot transition from " + status + " to CONFIRMED");
        }
    }

    public void markPaymentProcessing() {
        if (status.canTransitionTo(OrderStatus.PAYMENT_PROCESSING)) {
            this.status = OrderStatus.PAYMENT_PROCESSING;
        } else {
            throw new IllegalStateException("Cannot transition from " + status + " to PAYMENT_PROCESSING");
        }
    }

    public void complete(Long paymentId) {
        if (status.canTransitionTo(OrderStatus.COMPLETED)) {
            this.status = OrderStatus.COMPLETED;
            this.paymentId = paymentId;
        } else {
            throw new IllegalStateException("Cannot transition from " + status + " to COMPLETED");
        }
    }

    public void fail(String reason) {
        if (status.canTransitionTo(OrderStatus.FAILED)) {
            this.status = OrderStatus.FAILED;
            this.failureReason = reason;
        } else {
            throw new IllegalStateException("Cannot transition from " + status + " to FAILED");
        }
    }

    public void cancel(String reason) {
        if (status.canTransitionTo(OrderStatus.CANCELLED)) {
            this.status = OrderStatus.CANCELLED;
            this.failureReason = reason;
        } else {
            throw new IllegalStateException("Cannot transition from " + status + " to CANCELLED");
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}