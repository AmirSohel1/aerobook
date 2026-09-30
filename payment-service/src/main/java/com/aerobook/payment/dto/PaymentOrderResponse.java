package com.aerobook.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response containing Razorpay order credentials for client-side checkout")
public class PaymentOrderResponse {

    @Schema(description = "Razorpay Order ID", example = "order_Oq7xG7d9bB12")
    private String orderId;

    @Schema(description = "Amount in INR decimal", example = "4500.0")
    private Double amount;

    @Schema(description = "Amount in Paise for Razorpay Checkout JS", example = "450000")
    private Long amountInPaise;

    @Schema(description = "Currency code", example = "INR")
    private String currency;

    @Schema(description = "Public Razorpay Key ID for client SDK initialization", example = "rzp_test_1DP5mmOlF5G5ag")
    private String keyId;

    @Schema(description = "Associated flight booking ID", example = "101")
    private Long bookingId;

    @Schema(description = "PNR of the flight booking", example = "AB-942104")
    private String pnr;

    public PaymentOrderResponse() {
    }

    public PaymentOrderResponse(String orderId, Double amount, Long amountInPaise, String currency, String keyId, Long bookingId, String pnr) {
        this.orderId = orderId;
        this.amount = amount;
        this.amountInPaise = amountInPaise;
        this.currency = currency;
        this.keyId = keyId;
        this.bookingId = bookingId;
        this.pnr = pnr;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Long getAmountInPaise() {
        return amountInPaise;
    }

    public void setAmountInPaise(Long amountInPaise) {
        this.amountInPaise = amountInPaise;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getKeyId() {
        return keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }
}
