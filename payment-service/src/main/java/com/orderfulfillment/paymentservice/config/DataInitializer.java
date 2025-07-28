package com.orderfulfillment.paymentservice.config;

import com.orderfulfillment.paymentservice.entity.Payment;
import com.orderfulfillment.paymentservice.enums.PaymentStatus;
import com.orderfulfillment.paymentservice.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@Profile("local") // Only runs in local profile
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    private final PaymentRepository paymentRepository;

    @Autowired
    public DataInitializer(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (paymentRepository.count() == 0) {
            logger.info("Initializing sample payment data...");

            // Payment 1 - Completed
            Payment payment1 = new Payment(1001L, new BigDecimal("99.99"), "CREDIT_CARD");
            payment1.setStatus(PaymentStatus.COMPLETED);
            payment1.setTransactionId("TXN-12345678");
            payment1.setProcessedAt(LocalDateTime.now().minusHours(2));

            // Payment 2 - Pending (will be processed by scheduler)
            Payment payment2 = new Payment(1002L, new BigDecimal("149.50"), "DEBIT_CARD");
            // Leave as PENDING - scheduler will process this

            // Payment 3 - Failed
            Payment payment3 = new Payment(1003L, new BigDecimal("299.00"), "CREDIT_CARD");
            payment3.setStatus(PaymentStatus.FAILED);
            payment3.setFailureReason("Insufficient funds");
            payment3.setProcessedAt(LocalDateTime.now().minusHours(1));

            // Payment 4 - Processing
            Payment payment4 = new Payment(1004L, new BigDecimal("75.25"), "PAYPAL");
            payment4.setStatus(PaymentStatus.PROCESSING);

            // Payment 5 - Pending (older, for scheduler testing)
            Payment payment5 = new Payment(1005L, new BigDecimal("199.99"), "BANK_TRANSFER");
            // This will be picked up by scheduler due to age

            paymentRepository.save(payment1);
            paymentRepository.save(payment2);
            paymentRepository.save(payment3);
            paymentRepository.save(payment4);
            paymentRepository.save(payment5);

            // Manually set created time for payment5 to make it eligible for processing
            Payment savedPayment5 = paymentRepository.findByOrderId(1005L).orElse(null);
            if (savedPayment5 != null) {
                savedPayment5.setCreatedAt(LocalDateTime.now().minusMinutes(5));
                paymentRepository.save(savedPayment5);
            }

            logger.info("Sample payment data initialized successfully");
            logger.info("Created {} payments with various statuses", paymentRepository.count());
        } else {
            logger.info("Payment data already exists, skipping initialization");
        }
    }
}