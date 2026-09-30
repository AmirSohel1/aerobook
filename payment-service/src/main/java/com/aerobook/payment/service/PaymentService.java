package com.aerobook.payment.service;

import java.util.List;

import com.aerobook.payment.dto.PaymentOrderRequest;
import com.aerobook.payment.dto.PaymentOrderResponse;
import com.aerobook.payment.dto.PaymentRefundRequest;
import com.aerobook.payment.dto.PaymentRefundResponse;
import com.aerobook.payment.dto.PaymentVerifyRequest;
import com.aerobook.payment.dto.PaymentVerifyResponse;
import com.aerobook.payment.entity.PaymentRecord;

public interface PaymentService {

    PaymentOrderResponse createOrder(PaymentOrderRequest request);

    PaymentVerifyResponse verifyPayment(PaymentVerifyRequest request);

    PaymentRefundResponse processRefund(PaymentRefundRequest request);

    String handleWebhook(String payload, String signature);

    List<PaymentRecord> getAllPayments();

    PaymentRecord getPaymentById(Long id);

    List<PaymentRecord> getPaymentsByBookingId(Long bookingId);
}
