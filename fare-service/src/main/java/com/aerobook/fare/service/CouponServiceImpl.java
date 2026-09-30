package com.aerobook.fare.service;

import com.aerobook.fare.dto.request.CouponRequest;
import com.aerobook.fare.dto.request.CouponValidateRequest;
import com.aerobook.fare.dto.response.CouponResponse;
import com.aerobook.fare.dto.response.CouponValidateResponse;
import com.aerobook.fare.entity.Coupon;
import com.aerobook.fare.exception.ResourceNotFoundException;
import com.aerobook.fare.repository.CouponRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;

    public CouponServiceImpl(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @PostConstruct
    public void seedInitialCoupons() {
        if (couponRepository.count() == 0) {
            Coupon c1 = new Coupon();
            c1.setCode("AERO2026");
            c1.setDescription("New Year Early Bird Special - 15% Off");
            c1.setDiscountPercentage(new BigDecimal("15.0"));
            c1.setMaxDiscount(new BigDecimal("2500.00"));
            c1.setMinFare(new BigDecimal("3000.00"));
            c1.setUsageLimit(1000);
            c1.setUsageCount(240);
            c1.setValidFrom(LocalDate.of(2026, 1, 1));
            c1.setValidUntil(LocalDate.of(2026, 12, 31));
            c1.setStatus("ACTIVE");
            couponRepository.save(c1);

            Coupon c2 = new Coupon();
            c2.setCode("FLYHIGH20");
            c2.setDescription("Business & First Class Premium Upgrade - 20% Off");
            c2.setDiscountPercentage(new BigDecimal("20.0"));
            c2.setMaxDiscount(new BigDecimal("5000.00"));
            c2.setMinFare(new BigDecimal("6000.00"));
            c2.setUsageLimit(500);
            c2.setUsageCount(88);
            c2.setValidFrom(LocalDate.of(2026, 1, 1));
            c2.setValidUntil(LocalDate.of(2026, 12, 31));
            c2.setStatus("ACTIVE");
            couponRepository.save(c2);

            Coupon c3 = new Coupon();
            c3.setCode("GOLD15");
            c3.setDescription("Gold Aviator Tier Exclusive Discount - 15% Off");
            c3.setDiscountPercentage(new BigDecimal("15.0"));
            c3.setMaxDiscount(new BigDecimal("3000.00"));
            c3.setMinFare(new BigDecimal("2500.00"));
            c3.setUsageLimit(2000);
            c3.setUsageCount(150);
            c3.setValidFrom(LocalDate.of(2026, 1, 1));
            c3.setValidUntil(LocalDate.of(2026, 12, 31));
            c3.setStatus("ACTIVE");
            couponRepository.save(c3);

            Coupon c4 = new Coupon();
            c4.setCode("SPECIAL15");
            c4.setDescription("Quest Completion Reward Voucher - 15% Off");
            c4.setDiscountPercentage(new BigDecimal("15.0"));
            c4.setMaxDiscount(new BigDecimal("2000.00"));
            c4.setMinFare(new BigDecimal("2000.00"));
            c4.setUsageLimit(5000);
            c4.setUsageCount(310);
            c4.setValidFrom(LocalDate.of(2026, 1, 1));
            c4.setValidUntil(LocalDate.of(2026, 12, 31));
            c4.setStatus("ACTIVE");
            couponRepository.save(c4);
        }
    }

    @Override
    public CouponResponse createCoupon(CouponRequest request) {
        String code = request.getCode().trim().toUpperCase();
        if (couponRepository.existsByCodeIgnoreCase(code)) {
            throw new IllegalArgumentException("Coupon code '" + code + "' already exists.");
        }

        Coupon coupon = new Coupon();
        coupon.setCode(code);
        coupon.setDescription(request.getDescription());
        coupon.setDiscountPercentage(request.getDiscountPercentage());
        coupon.setMaxDiscount(request.getMaxDiscount());
        coupon.setMinFare(request.getMinFare() != null ? request.getMinFare() : BigDecimal.ZERO);
        coupon.setUsageLimit(request.getUsageLimit() != null ? request.getUsageLimit() : 1000);
        coupon.setValidFrom(request.getValidFrom() != null ? request.getValidFrom() : LocalDate.now());
        coupon.setValidUntil(request.getValidUntil() != null ? request.getValidUntil() : LocalDate.now().plusMonths(6));
        coupon.setStatus(request.getStatus() != null ? request.getStatus().toUpperCase() : "ACTIVE");

        Coupon saved = couponRepository.save(coupon);
        return new CouponResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(CouponResponse::new)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse getCouponById(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with ID: " + id));
        return new CouponResponse(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponse getCouponByCode(String code) {
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code.trim())
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with code: " + code));
        return new CouponResponse(coupon);
    }

    @Override
    public CouponResponse updateCoupon(Long id, CouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with ID: " + id));

        if (request.getDescription() != null) coupon.setDescription(request.getDescription());
        if (request.getDiscountPercentage() != null) coupon.setDiscountPercentage(request.getDiscountPercentage());
        if (request.getMaxDiscount() != null) coupon.setMaxDiscount(request.getMaxDiscount());
        if (request.getMinFare() != null) coupon.setMinFare(request.getMinFare());
        if (request.getUsageLimit() != null) coupon.setUsageLimit(request.getUsageLimit());
        if (request.getValidFrom() != null) coupon.setValidFrom(request.getValidFrom());
        if (request.getValidUntil() != null) coupon.setValidUntil(request.getValidUntil());
        if (request.getStatus() != null) coupon.setStatus(request.getStatus().toUpperCase());

        Coupon updated = couponRepository.save(coupon);
        return new CouponResponse(updated);
    }

    @Override
    public CouponResponse toggleCouponStatus(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found with ID: " + id));

        String newStatus = "ACTIVE".equalsIgnoreCase(coupon.getStatus()) ? "PAUSED" : "ACTIVE";
        coupon.setStatus(newStatus);
        return new CouponResponse(couponRepository.save(coupon));
    }

    @Override
    public void deleteCoupon(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new ResourceNotFoundException("Coupon not found with ID: " + id);
        }
        couponRepository.deleteById(id);
    }

    @Override
    public CouponValidateResponse validateCoupon(CouponValidateRequest request) {
        String code = request.getCode().trim().toUpperCase();
        var opt = couponRepository.findByCodeIgnoreCase(code);

        if (opt.isEmpty()) {
            return new CouponValidateResponse(false, code, BigDecimal.ZERO, BigDecimal.ZERO, request.getBaseFare(), "Invalid promo code.");
        }

        Coupon coupon = opt.get();

        if (!"ACTIVE".equalsIgnoreCase(coupon.getStatus())) {
            return new CouponValidateResponse(false, code, BigDecimal.ZERO, BigDecimal.ZERO, request.getBaseFare(), "This promo code is currently inactive.");
        }

        LocalDate today = LocalDate.now();
        if (coupon.getValidFrom() != null && today.isBefore(coupon.getValidFrom())) {
            return new CouponValidateResponse(false, code, BigDecimal.ZERO, BigDecimal.ZERO, request.getBaseFare(), "Promo code is not yet valid.");
        }
        if (coupon.getValidUntil() != null && today.isAfter(coupon.getValidUntil())) {
            return new CouponValidateResponse(false, code, BigDecimal.ZERO, BigDecimal.ZERO, request.getBaseFare(), "Promo code has expired.");
        }

        if (coupon.getUsageLimit() != null && coupon.getUsageCount() >= coupon.getUsageLimit()) {
            return new CouponValidateResponse(false, code, BigDecimal.ZERO, BigDecimal.ZERO, request.getBaseFare(), "Promo code maximum usage limit reached.");
        }

        if (coupon.getMinFare() != null && request.getBaseFare().compareTo(coupon.getMinFare()) < 0) {
            return new CouponValidateResponse(false, code, BigDecimal.ZERO, BigDecimal.ZERO, request.getBaseFare(),
                    "Minimum ticket fare of ₹" + coupon.getMinFare() + " required to use this coupon.");
        }

        // Calculate discount percentage
        BigDecimal percentageMultiplier = coupon.getDiscountPercentage().divide(new BigDecimal("100.0"), 4, RoundingMode.HALF_UP);
        BigDecimal rawDiscount = request.getBaseFare().multiply(percentageMultiplier).setScale(2, RoundingMode.HALF_UP);

        // Cap at maxDiscount
        BigDecimal finalDiscount = rawDiscount;
        if (coupon.getMaxDiscount() != null && rawDiscount.compareTo(coupon.getMaxDiscount()) > 0) {
            finalDiscount = coupon.getMaxDiscount();
        }

        BigDecimal finalFare = request.getBaseFare().subtract(finalDiscount).setScale(2, RoundingMode.HALF_UP);
        if (finalFare.compareTo(BigDecimal.ZERO) < 0) {
            finalFare = BigDecimal.ZERO;
        }

        return new CouponValidateResponse(true, code, coupon.getDiscountPercentage(), finalDiscount, finalFare, "Coupon applied successfully!");
    }
}
