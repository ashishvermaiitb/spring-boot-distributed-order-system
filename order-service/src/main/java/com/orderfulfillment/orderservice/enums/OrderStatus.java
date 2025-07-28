package com.orderfulfillment.orderservice.enums;

public enum OrderStatus {
    PENDING("Order is pending processing"),
    CONFIRMED("Order has been confirmed"),
    PAYMENT_PROCESSING("Payment is being processed"),
    COMPLETED("Order has been completed successfully"),
    FAILED("Order processing failed"),
    CANCELLED("Order has been cancelled");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isTerminalStatus() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }

    public boolean canTransitionTo(OrderStatus newStatus) {
        switch (this) {
            case PENDING:
                return newStatus == CONFIRMED || newStatus == FAILED || newStatus == CANCELLED;
            case CONFIRMED:
                return newStatus == PAYMENT_PROCESSING || newStatus == FAILED || newStatus == CANCELLED;
            case PAYMENT_PROCESSING:
                return newStatus == COMPLETED || newStatus == FAILED || newStatus == CANCELLED;
            case COMPLETED:
            case FAILED:
            case CANCELLED:
                return false; // Terminal states cannot transition
            default:
                return false;
        }
    }
}