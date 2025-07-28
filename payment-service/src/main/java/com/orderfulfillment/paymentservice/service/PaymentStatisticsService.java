package com.orderfulfillment.paymentservice.service;

import com.orderfulfillment.paymentservice.enums.PaymentStatus;
import com.orderfulfillment.paymentservice.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentStatisticsService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentStatisticsService.class);

    private final PaymentRepository paymentRepository;

    @Autowired
    public PaymentStatisticsService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Map<PaymentStatus, Long> getPaymentCountsByStatus() {
        logger.debug("Fetching payment counts by status");

        Map<PaymentStatus, Long> stats = new HashMap<>();

        for (PaymentStatus status : PaymentStatus.values()) {
            long count = paymentRepository.countByStatus(status);
            stats.put(status, count);
        }

        return stats;
    }

    public void logPaymentStatistics() {
        Map<PaymentStatus, Long> stats = getPaymentCountsByStatus();

        logger.info("=== Payment Statistics ===");
        stats.forEach((status, count) ->
                logger.info("{}: {} payments", status, count)
        );

        long totalPayments = stats.values().stream().mapToLong(Long::longValue).sum();
        logger.info("Total payments: {}", totalPayments);
        logger.info("==========================");
    }
}