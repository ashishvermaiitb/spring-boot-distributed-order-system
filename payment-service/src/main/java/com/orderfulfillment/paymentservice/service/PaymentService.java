package com.orderfulfillment.paymentservice.service;

import com.orderfulfillment.paymentservice.dto.PaymentRequestDto;
import com.orderfulfillment.paymentservice.dto.PaymentResponseDto;
import com.orderfulfillment.paymentservice.enums.PaymentStatus;

import java.util.List;

public interface PaymentService {

    PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto);

    PaymentResponseDto getPaymentById(Long paymentId);

    PaymentResponseDto getPaymentByOrderId(Long orderId);

    List<PaymentResponseDto> getAllPayments();

    List<PaymentResponseDto> getPaymentsByStatus(PaymentStatus status);

    void processEligiblePayments();

    boolean existsByOrderId(Long orderId);
}