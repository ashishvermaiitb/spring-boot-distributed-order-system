package com.orderfulfillment.paymentservice.enums;

public enum PaymentStatus {
    PENDING("Payment is pending processing"),
    PROCESSING("Payment is being processed"),
    COMPLETED("Payment has been completed successfully"),
    FAILED("Payment processing failed"),
    CANCELLED("Payment has been cancelled");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isTerminalStatus() {
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }

    public boolean canTransitionTo(PaymentStatus newStatus) {
        switch (this) {
            case PENDING:
                return newStatus == PROCESSING || newStatus == FAILED || newStatus == CANCELLED;
            case PROCESSING:
                return newStatus == COMPLETED || newStatus == FAILED;
            case COMPLETED:
            case FAILED:
            case CANCELLED:
                return false; // Terminal states cannot transition
            default:
                return false;
        }
    }
}