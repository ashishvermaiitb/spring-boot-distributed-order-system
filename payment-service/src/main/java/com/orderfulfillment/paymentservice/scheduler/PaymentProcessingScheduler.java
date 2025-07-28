package com.orderfulfillment.paymentservice.scheduler;

import com.orderfulfillment.paymentservice.service.PaymentService;
import com.orderfulfillment.paymentservice.service.PaymentStatisticsService;
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
    private final PaymentStatisticsService paymentStatisticsService;

    @Value("${payment.processing.batch-size:10}")
    private int batchSize;

    @Autowired
    public PaymentProcessingScheduler(PaymentService paymentService,
                                      PaymentStatisticsService paymentStatisticsService) {
        this.paymentService = paymentService;
        this.paymentStatisticsService = paymentStatisticsService;
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
        logger.debug("Running payment statistics logging job");
        try {
            paymentStatisticsService.logPaymentStatistics();
        } catch (Exception e) {
            logger.error("Error occurred during payment statistics logging", e);
        }
    }
}