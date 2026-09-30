package com.aerobook.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to initiate refund for a captured payment")
public class PaymentRefundRequest {

    @Schema(description = "Payment ID to refund", example = "pay_Oq7zZ4n1mC99")
    @NotBlank(message = "Payment ID is required")
    private String paymentId;

    @Schema(description = "Amount in INR to refund (optional, defaults to full amount)", example = "4500.0")
    private Double amount;

    @Schema(description = "Reason for refund", example = "Flight cancellation by passenger")
    private String reason;

    public PaymentRefundRequest() {
    }

    public PaymentRefundRequest(String paymentId, Double amount, String reason) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.reason = reason;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
