package com.aerobook.payment.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aerobook.payment.dto.PaymentOrderRequest;
import com.aerobook.payment.dto.PaymentOrderResponse;
import com.aerobook.payment.dto.PaymentRefundRequest;
import com.aerobook.payment.dto.PaymentRefundResponse;
import com.aerobook.payment.dto.PaymentVerifyRequest;
import com.aerobook.payment.dto.PaymentVerifyResponse;
import com.aerobook.payment.entity.PaymentRecord;
import com.aerobook.payment.service.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Razorpay Payment Service", description = "Operations for Razorpay order generation, cryptographic HMAC signature verification, refunds, and webhooks")
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(summary = "Service health check", description = "Operational probe for payment service")
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Payment Service is running!");
    }

    @Operation(summary = "Create Razorpay Order", description = "Generates Razorpay order ID and returns public key ID for frontend checkout modal")
    @PostMapping("/orders")
    public ResponseEntity<PaymentOrderResponse> createOrder(@Valid @RequestBody PaymentOrderRequest request) {
        PaymentOrderResponse response = paymentService.createOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @Operation(summary = "Verify Razorpay Payment", description = "Validates client signature using HMAC-SHA256, marks payment as CAPTURED")
    @PostMapping("/verify")
    public ResponseEntity<PaymentVerifyResponse> verifyPayment(@Valid @RequestBody PaymentVerifyRequest request) {
        PaymentVerifyResponse response = paymentService.verifyPayment(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Razorpay Webhook Handler", description = "Receives server-to-server asynchronous notifications from Razorpay")
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "X-Razorpay-Signature", required = false) String signature) {
        String result = paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Process Payment Refund", description = "Issues a refund for a previously captured payment")
    @PostMapping("/{id}/refund")
    public ResponseEntity<PaymentRefundResponse> processRefund(
            @PathVariable Long id,
            @RequestBody(required = false) PaymentRefundRequest request) {
        PaymentRecord record = paymentService.getPaymentById(id);
        PaymentRefundRequest refundReq = (request != null) ? request : new PaymentRefundRequest();
        refundReq.setPaymentId(record.getPaymentId());
        PaymentRefundResponse response = paymentService.processRefund(refundReq);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "List all payments", description = "Administrative audit view of all transactions")
    @GetMapping
    public ResponseEntity<List<PaymentRecord>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @Operation(summary = "Get payment by ID")
    @GetMapping("/{id}")
    public ResponseEntity<PaymentRecord> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @Operation(summary = "Get payment by flight booking ID")
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<List<PaymentRecord>> getPaymentsByBookingId(@PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.getPaymentsByBookingId(bookingId));
    }
}
