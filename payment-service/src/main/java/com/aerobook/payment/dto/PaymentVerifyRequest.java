package com.aerobook.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Verification payload containing Razorpay client checkout callback tokens")
public class PaymentVerifyRequest {

    @Schema(description = "Razorpay Order ID", example = "order_Oq7xG7d9bB12")
    @NotBlank(message = "Razorpay order ID is required")
    private String razorpayOrderId;

    @Schema(description = "Razorpay Payment ID returned from Checkout modal", example = "pay_Oq7zZ4n1mC99")
    @NotBlank(message = "Razorpay payment ID is required")
    private String razorpayPaymentId;

    @Schema(description = "Razorpay cryptographic signature returned from Checkout modal", example = "92b8...a7e3")
    @NotBlank(message = "Razorpay signature is required")
    private String razorpaySignature;

    @Schema(description = "Associated flight booking ID", example = "101")
    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    public PaymentVerifyRequest() {
    }

    public PaymentVerifyRequest(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature, Long bookingId) {
        this.razorpayOrderId = razorpayOrderId;
        this.razorpayPaymentId = razorpayPaymentId;
        this.razorpaySignature = razorpaySignature;
        this.bookingId = bookingId;
    }

    public String getRazorpayOrderId() {
        return razorpayOrderId;
    }

    public void setRazorpayOrderId(String razorpayOrderId) {
        this.razorpayOrderId = razorpayOrderId;
    }

    public String getRazorpayPaymentId() {
        return razorpayPaymentId;
    }

    public void setRazorpayPaymentId(String razorpayPaymentId) {
        this.razorpayPaymentId = razorpayPaymentId;
    }

    public String getRazorpaySignature() {
        return razorpaySignature;
    }

    public void setRazorpaySignature(String razorpaySignature) {
        this.razorpaySignature = razorpaySignature;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }
}
