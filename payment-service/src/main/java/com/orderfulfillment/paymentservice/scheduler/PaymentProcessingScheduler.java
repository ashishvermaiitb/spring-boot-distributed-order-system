package com.orderfulfillment.paymentservice.scheduler;

import com.orderfulfillment.paymentservice.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PaymentProcessingScheduler {

    private static final Logger logger = LoggerFactory.getLogger(PaymentProcessingScheduler.class);

    private final PaymentService paymentService;

    @Value("${payment.processing.batch-size:10}")
    private int batchSize;

    @Autowired
    public PaymentProcessingScheduler(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Scheduled(fixedDelayString = "${payment.processing.scheduled-delay:60000}")
    public void processEligiblePayments() {
        logger.info("Starting scheduled payment processing job");

        try {
            paymentService.processEligiblePayments();
            logger.info("Completed scheduled payment processing job successfully");

        } catch (Exception e) {
            logger.error("Error occurred during scheduled payment processing", e);
        }
    }

    @Scheduled(cron = "0 */5 * * * *") // Every 5 minutes
    public void logPaymentStatistics() {
        logger.info("Payment processing statistics will be logged here");
        // This can be extended to log payment counts by status
    }
}