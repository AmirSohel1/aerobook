package com.aerobook.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Schema(description = "Payload to initiate a Razorpay payment order for a flight booking")
public class PaymentOrderRequest {

    @Schema(description = "Booking database identifier", example = "101")
    @NotNull(message = "Booking ID is required")
    private Long bookingId;

    @Schema(description = "Amount in INR decimal", example = "4500.0")
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be greater than zero")
    private Double amount;

    @Schema(description = "Currency code (default INR)", example = "INR")
    private String currency = "INR";

    @Schema(description = "Receipt identifier or reference", example = "RCP-101")
    private String receipt;

    @Schema(description = "PNR of the flight booking", example = "AB-942104")
    private String pnr;

    public PaymentOrderRequest() {
    }

    public PaymentOrderRequest(Long bookingId, Double amount, String currency, String receipt, String pnr) {
        this.bookingId = bookingId;
        this.amount = amount;
        this.currency = currency != null ? currency : "INR";
        this.receipt = receipt;
        this.pnr = pnr;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getPnr() {
        return pnr;
    }

    public void setPnr(String pnr) {
        this.pnr = pnr;
    }
}
