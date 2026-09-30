package com.aerobook.fare.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CouponValidateRequest {

    @NotBlank(message = "Coupon code is required")
    private String code;

    private Long flightId;

    @NotNull(message = "Base fare is required")
    @DecimalMin(value = "0.0", message = "Base fare cannot be negative")
    private BigDecimal baseFare;

    private String cabinClass;

    public CouponValidateRequest() {}

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }

    public BigDecimal getBaseFare() { return baseFare; }
    public void setBaseFare(BigDecimal baseFare) { this.baseFare = baseFare; }

    public String getCabinClass() { return cabinClass; }
    public void setCabinClass(String cabinClass) { this.cabinClass = cabinClass; }
}
