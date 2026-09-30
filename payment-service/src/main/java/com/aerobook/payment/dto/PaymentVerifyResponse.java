package com.aerobook.payment.dto;

import com.aerobook.payment.entity.PaymentStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Payment verification confirmation response")
public class PaymentVerifyResponse {

    @Schema(description = "Whether the HMAC-SHA256 signature is authentic and verified", example = "true")
    private boolean verified;

    @Schema(description = "Razorpay Payment ID", example = "pay_Oq7zZ4n1mC99")
    private String paymentId;

    @Schema(description = "Razorpay Order ID", example = "order_Oq7xG7d9bB12")
    private String orderId;

    @Schema(description = "Flight booking ID", example = "101")
    private Long bookingId;

    @Schema(description = "Updated payment status", example = "CAPTURED")
    private PaymentStatus status;

    @Schema(description = "Status explanation message", example = "Payment verified successfully and flight ticket confirmed")
    private String message;

    public PaymentVerifyResponse() {
    }

    public PaymentVerifyResponse(boolean verified, String paymentId, String orderId, Long bookingId, PaymentStatus status, String message) {
        this.verified = verified;
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.bookingId = bookingId;
        this.status = status;
        this.message = message;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
