package com.aerobook.fare.dto.response;

import com.aerobook.fare.entity.Coupon;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CouponResponse {

    private Long id;
    private String code;
    private String description;
    private BigDecimal discountPercentage;
    private BigDecimal maxDiscount;
    private BigDecimal minFare;
    private Integer usageLimit;
    private Integer usageCount;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private String status;
    private LocalDateTime createdAt;

    public CouponResponse() {}

    public CouponResponse(Coupon entity) {
        this.id = entity.getId();
        this.code = entity.getCode();
        this.description = entity.getDescription();
        this.discountPercentage = entity.getDiscountPercentage();
        this.maxDiscount = entity.getMaxDiscount();
        this.minFare = entity.getMinFare();
        this.usageLimit = entity.getUsageLimit();
        this.usageCount = entity.getUsageCount();
        this.validFrom = entity.getValidFrom();
        this.validUntil = entity.getValidUntil();
        this.status = entity.getStatus();
        this.createdAt = entity.getCreatedAt();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }

    public BigDecimal getMaxDiscount() { return maxDiscount; }
    public void setMaxDiscount(BigDecimal maxDiscount) { this.maxDiscount = maxDiscount; }

    public BigDecimal getMinFare() { return minFare; }
    public void setMinFare(BigDecimal minFare) { this.minFare = minFare; }

    public Integer getUsageLimit() { return usageLimit; }
    public void setUsageLimit(Integer usageLimit) { this.usageLimit = usageLimit; }

    public Integer getUsageCount() { return usageCount; }
    public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }

    public LocalDate getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDate validFrom) { this.validFrom = validFrom; }

    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
