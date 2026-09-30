package com.aerobook.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Response acknowledging refund processing")
public class PaymentRefundResponse {

    @Schema(description = "Unique refund identifier", example = "rfnd_Oq8aBc12dE34")
    private String refundId;

    @Schema(description = "Associated Payment ID", example = "pay_Oq7zZ4n1mC99")
    private String paymentId;

    @Schema(description = "Amount refunded in INR", example = "4500.0")
    private Double amount;

    @Schema(description = "Refund processing status", example = "PROCESSED")
    private String status;

    public PaymentRefundResponse() {
    }

    public PaymentRefundResponse(String refundId, String paymentId, Double amount, String status) {
        this.refundId = refundId;
        this.paymentId = paymentId;
        this.amount = amount;
        this.status = status;
    }

    public String getRefundId() {
        return refundId;
    }

    public void setRefundId(String refundId) {
        this.refundId = refundId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
