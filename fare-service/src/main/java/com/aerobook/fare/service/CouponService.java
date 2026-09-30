package com.aerobook.fare.service;

import com.aerobook.fare.dto.request.CouponRequest;
import com.aerobook.fare.dto.request.CouponValidateRequest;
import com.aerobook.fare.dto.response.CouponResponse;
import com.aerobook.fare.dto.response.CouponValidateResponse;

import java.util.List;

public interface CouponService {
    CouponResponse createCoupon(CouponRequest request);
    List<CouponResponse> getAllCoupons();
    CouponResponse getCouponById(Long id);
    CouponResponse getCouponByCode(String code);
    CouponResponse updateCoupon(Long id, CouponRequest request);
    CouponResponse toggleCouponStatus(Long id);
    void deleteCoupon(Long id);
    CouponValidateResponse validateCoupon(CouponValidateRequest request);
}
