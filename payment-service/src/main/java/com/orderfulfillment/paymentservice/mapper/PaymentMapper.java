package com.orderfulfillment.paymentservice.mapper;

import com.orderfulfillment.paymentservice.dto.PaymentRequestDto;
import com.orderfulfillment.paymentservice.dto.PaymentResponseDto;
import com.orderfulfillment.paymentservice.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    /**
     * Convert PaymentRequestDto to Payment Entity
     */
    public Payment toEntity(PaymentRequestDto paymentRequestDto) {
        if (paymentRequestDto == null) {
            return null;
        }

        Payment payment = new Payment();
        payment.setOrderId(paymentRequestDto.getOrderId());
        payment.setAmount(paymentRequestDto.getAmount());
        payment.setPaymentMethod(paymentRequestDto.getPaymentMethod());

        return payment;
    }

    /**
     * Convert Payment Entity to PaymentResponseDto
     */
    public PaymentResponseDto toResponseDto(Payment payment) {
        if (payment == null) {
            return null;
        }

        PaymentResponseDto responseDto = new PaymentResponseDto();
        responseDto.setId(payment.getId());
        responseDto.setOrderId(payment.getOrderId());
        responseDto.setAmount(payment.getAmount());
        responseDto.setStatus(payment.getStatus());
        responseDto.setPaymentMethod(payment.getPaymentMethod());
        responseDto.setTransactionId(payment.getTransactionId());
        responseDto.setFailureReason(payment.getFailureReason());
        responseDto.setProcessedAt(payment.getProcessedAt());
        responseDto.setCreatedAt(payment.getCreatedAt());
        responseDto.setUpdatedAt(payment.getUpdatedAt());

        return responseDto;
    }
}