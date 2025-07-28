package com.orderfulfillment.paymentservice.repository;

import com.orderfulfillment.paymentservice.entity.Payment;
import com.orderfulfillment.paymentservice.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    List<Payment> findByStatus(PaymentStatus status);

    List<Payment> findByStatusIn(List<PaymentStatus> statuses);

    @Query("SELECT p FROM Payment p WHERE p.status = :status AND p.createdAt <= :cutoffTime")
    List<Payment> findEligibleForProcessing(@Param("status") PaymentStatus status,
                                            @Param("cutoffTime") LocalDateTime cutoffTime);

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.status = :status")
    long countByStatus(@Param("status") PaymentStatus status);

    boolean existsByOrderId(Long orderId);
}