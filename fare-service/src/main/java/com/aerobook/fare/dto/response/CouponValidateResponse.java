package com.aerobook.fare.dto.response;

import java.math.BigDecimal;

public class CouponValidateResponse {

    private boolean valid;
    private String code;
    private BigDecimal discountPercentage;
    private BigDecimal discountAmount;
    private BigDecimal finalFare;
    private String message;

    public CouponValidateResponse() {}

    public CouponValidateResponse(boolean valid, String code, BigDecimal discountPercentage, BigDecimal discountAmount, BigDecimal finalFare, String message) {
        this.valid = valid;
        this.code = code;
        this.discountPercentage = discountPercentage;
        this.discountAmount = discountAmount;
        this.finalFare = finalFare;
        this.message = message;
    }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getFinalFare() { return finalFare; }
    public void setFinalFare(BigDecimal finalFare) { this.finalFare = finalFare; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
