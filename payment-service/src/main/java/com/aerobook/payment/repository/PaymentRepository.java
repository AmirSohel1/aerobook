package com.aerobook.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.aerobook.payment.entity.PaymentRecord;
import com.aerobook.payment.entity.PaymentStatus;

@Repository
public interface PaymentRepository extends JpaRepository<PaymentRecord, Long> {

    Optional<PaymentRecord> findByOrderId(String orderId);

    Optional<PaymentRecord> findByPaymentId(String paymentId);

    List<PaymentRecord> findByBookingId(Long bookingId);

    List<PaymentRecord> findByStatus(PaymentStatus status);
}
