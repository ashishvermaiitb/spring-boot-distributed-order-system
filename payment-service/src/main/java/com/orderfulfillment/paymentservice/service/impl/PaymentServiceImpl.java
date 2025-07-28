package com.orderfulfillment.paymentservice.service.impl;

import com.orderfulfillment.paymentservice.dto.PaymentRequestDto;
import com.orderfulfillment.paymentservice.dto.PaymentResponseDto;
import com.orderfulfillment.paymentservice.entity.Payment;
import com.orderfulfillment.paymentservice.enums.PaymentStatus;
import com.orderfulfillment.paymentservice.exception.PaymentNotFoundException;
import com.orderfulfillment.paymentservice.mapper.PaymentMapper;
import com.orderfulfillment.paymentservice.repository.PaymentRepository;
import com.orderfulfillment.paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository, PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto) {
        logger.info("Creating payment for order ID: {}", paymentRequestDto.getOrderId());

        // Convert DTO to Entity
        Payment payment = paymentMapper.toEntity(paymentRequestDto);

        // Save payment
        Payment savedPayment = paymentRepository.save(payment);
        logger.info("Payment created with ID: {} for order ID: {}",
                savedPayment.getId(), savedPayment.getOrderId());

        return paymentMapper.toResponseDto(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentById(Long paymentId) {
        logger.info("Fetching payment with ID: {}", paymentId);

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> {
                    logger.error("Payment not found with ID: {}", paymentId);
                    return new PaymentNotFoundException("Payment not found with ID: " + paymentId);
                });

        return paymentMapper.toResponseDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentByOrderId(Long orderId) {
        logger.info("Fetching payment for order ID: {}", orderId);

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> {
                    logger.error("Payment not found for order ID: {}", orderId);
                    return new PaymentNotFoundException("Payment not found for order ID: " + orderId);
                });

        return paymentMapper.toResponseDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getAllPayments() {
        logger.info("Fetching all payments");

        List<Payment> payments = paymentRepository.findAll();
        logger.info("Found {} payments", payments.size());

        return payments.stream()
                .map(paymentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPaymentsByStatus(PaymentStatus status) {
        logger.info("Fetching payments with status: {}", status);

        List<Payment> payments = paymentRepository.findByStatus(status);
        logger.info("Found {} payments with status: {}", payments.size(), status);

        return payments.stream()
                .map(paymentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByOrderId(Long orderId) {
        logger.debug("Checking if payment exists for order ID: {}", orderId);
        boolean exists = paymentRepository.existsByOrderId(orderId);
        logger.debug("Payment exists for order ID {}: {}", orderId, exists);
        return exists;
    }

    @Override
    public void processEligiblePayments() {
        logger.info("Starting processing of eligible payments");

        // Find payments that are pending and older than 1 minute (for demo)
        LocalDateTime cutoffTime = LocalDateTime.now().minusMinutes(1);
        List<Payment> eligiblePayments = paymentRepository.findEligibleForProcessing(
                PaymentStatus.PENDING, cutoffTime);

        logger.info("Found {} eligible payments for processing", eligiblePayments.size());

        for (Payment payment : eligiblePayments) {
            processPayment(payment);
        }

        logger.info("Completed processing of eligible payments");
    }

    private void processPayment(Payment payment) {
        try {
            logger.info("Processing payment ID: {} for order ID: {}",
                    payment.getId(), payment.getOrderId());

            // Mark as processing
            payment.markAsProcessing();
            paymentRepository.save(payment);

            // Simulate payment processing (90% success rate for demo)
            boolean paymentSuccessful = Math.random() > 0.1;

            if (paymentSuccessful) {
                String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
                payment.markAsCompleted(transactionId);
                logger.info("Payment ID: {} completed successfully with transaction ID: {}",
                        payment.getId(), transactionId);
            } else {
                payment.markAsFailed("Payment processing failed - insufficient funds");
                logger.warn("Payment ID: {} failed processing", payment.getId());
            }

            paymentRepository.save(payment);

        } catch (Exception e) {
            logger.error("Error processing payment ID: {}", payment.getId(), e);
            try {
                payment.markAsFailed("Internal processing error: " + e.getMessage());
                paymentRepository.save(payment);
            } catch (Exception saveException) {
                logger.error("Failed to save payment failure state for ID: {}",
                        payment.getId(), saveException);
            }
        }
    }
}