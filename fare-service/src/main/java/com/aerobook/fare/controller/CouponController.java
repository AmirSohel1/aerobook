package com.aerobook.fare.controller;

import com.aerobook.fare.dto.request.CouponRequest;
import com.aerobook.fare.dto.request.CouponValidateRequest;
import com.aerobook.fare.dto.response.CouponResponse;
import com.aerobook.fare.dto.response.CouponValidateResponse;
import com.aerobook.fare.dto.response.ErrorResponse;
import com.aerobook.fare.service.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Coupons & Dynamic Pricing Promotions", description = "Endpoints for creating, managing, and validating discount coupons")
@RestController
@RequestMapping("/api/fares/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @Operation(summary = "List all promo codes", description = "Retrieves all coupon codes and status.")
    @GetMapping
    public ResponseEntity<List<CouponResponse>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }

    @Operation(summary = "Get coupon by ID")
    @GetMapping("/{id}")
    public ResponseEntity<CouponResponse> getCouponById(@PathVariable Long id) {
        return ResponseEntity.ok(couponService.getCouponById(id));
    }

    @Operation(summary = "Create coupon (Admin Only)", description = "Publishes a new promotional code with discount percentage and limits.")
    @PostMapping
    public ResponseEntity<?> createCoupon(
            @Valid @RequestBody CouponRequest request,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        if (userRole != null && !userRole.isEmpty() && !"ROLE_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                    LocalDateTime.now(), HttpStatus.FORBIDDEN.value(), "Forbidden",
                    "Creating coupons requires ROLE_ADMIN authority."
            ));
        }

        CouponResponse created = couponService.createCoupon(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @Operation(summary = "Update coupon (Admin Only)")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCoupon(
            @PathVariable Long id,
            @Valid @RequestBody CouponRequest request,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        if (userRole != null && !userRole.isEmpty() && !"ROLE_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                    LocalDateTime.now(), HttpStatus.FORBIDDEN.value(), "Forbidden",
                    "Modifying coupons requires ROLE_ADMIN authority."
            ));
        }

        return ResponseEntity.ok(couponService.updateCoupon(id, request));
    }

    @Operation(summary = "Toggle coupon status (Activate / Pause)")
    @PutMapping("/{id}/toggle")
    public ResponseEntity<?> toggleCouponStatus(
            @PathVariable Long id,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        if (userRole != null && !userRole.isEmpty() && !"ROLE_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                    LocalDateTime.now(), HttpStatus.FORBIDDEN.value(), "Forbidden",
                    "Toggling coupon status requires ROLE_ADMIN authority."
            ));
        }

        return ResponseEntity.ok(couponService.toggleCouponStatus(id));
    }

    @Operation(summary = "Delete coupon (Admin Only)")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCoupon(
            @PathVariable Long id,
            @Parameter(hidden = true)
            @RequestHeader(value = "X-User-Role", required = false) String userRole) {

        if (userRole != null && !userRole.isEmpty() && !"ROLE_ADMIN".equals(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(
                    LocalDateTime.now(), HttpStatus.FORBIDDEN.value(), "Forbidden",
                    "Deleting coupons requires ROLE_ADMIN authority."
            ));
        }

        couponService.deleteCoupon(id);
        return ResponseEntity.ok("Coupon deleted successfully with ID: " + id);
    }

    @Operation(summary = "Validate coupon and compute discount (Public / Customer)", description = "Validates promo code against rules and returns discount amount.")
    @PostMapping("/validate")
    public ResponseEntity<CouponValidateResponse> validateCoupon(@Valid @RequestBody CouponValidateRequest request) {
        return ResponseEntity.ok(couponService.validateCoupon(request));
    }
}
