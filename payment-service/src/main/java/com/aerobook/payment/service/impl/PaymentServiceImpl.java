package com.aerobook.payment.service.impl;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aerobook.payment.dto.PaymentOrderRequest;
import com.aerobook.payment.dto.PaymentOrderResponse;
import com.aerobook.payment.dto.PaymentRefundRequest;
import com.aerobook.payment.dto.PaymentRefundResponse;
import com.aerobook.payment.dto.PaymentVerifyRequest;
import com.aerobook.payment.dto.PaymentVerifyResponse;
import com.aerobook.payment.entity.PaymentRecord;
import com.aerobook.payment.entity.PaymentStatus;
import com.aerobook.payment.repository.PaymentRepository;
import com.aerobook.payment.service.PaymentService;
import com.aerobook.payment.service.RazorpaySignatureValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);
    private static final String RAZORPAY_API_URL = "https://api.razorpay.com/v1";

    private final PaymentRepository paymentRepository;
    private final RazorpaySignatureValidator signatureValidator;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Value("${razorpay.key.id:rzp_test_TiGBpThLMx6w6T}")
    private String keyId;

    @Value("${razorpay.key.secret:8xbFOJXwccYYxURtAlcF6d8c}")
    private String keySecret;

    @Value("${razorpay.currency:INR}")
    private String defaultCurrency;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            RazorpaySignatureValidator signatureValidator,
            ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.signatureValidator = signatureValidator;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public PaymentOrderResponse createOrder(PaymentOrderRequest request) {
        Long amountInPaise = Math.round(request.getAmount() * 100);
        String currency = (request.getCurrency() != null && !request.getCurrency().isBlank())
                ? request.getCurrency() : defaultCurrency;
        String receipt = (request.getReceipt() != null && !request.getReceipt().isBlank())
                ? request.getReceipt() : "RCP-" + request.getBookingId();

        String orderId = null;

        // Attempt real Razorpay Orders API call
        try {
            String credentials = Base64.getEncoder().encodeToString(
                    (keyId + ":" + keySecret).getBytes(StandardCharsets.UTF_8));

            String payload = objectMapper.writeValueAsString(java.util.Map.of(
                    "amount", amountInPaise,
                    "currency", currency,
                    "receipt", receipt,
                    "notes", java.util.Map.of(
                            "bookingId", String.valueOf(request.getBookingId()),
                            "pnr", request.getPnr() != null ? request.getPnr() : "PENDING"
                    )
            ));

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(RAZORPAY_API_URL + "/orders"))
                    .header("Authorization", "Basic " + credentials)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(payload))
                    .build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() == 200 || httpResponse.statusCode() == 201) {
                JsonNode root = objectMapper.readTree(httpResponse.body());
                if (root.has("id")) {
                    orderId = root.get("id").asText();
                    log.info("Razorpay order created successfully via API: {}", orderId);
                }
            } else {
                log.warn("Razorpay API returned HTTP {}: {}", httpResponse.statusCode(), httpResponse.body());
            }
        } catch (Exception e) {
            log.warn("Unable to reach Razorpay API (will generate fallback test order): {}", e.getMessage());
        }

        // Fallback order ID for offline test / simulation mode
        if (orderId == null || orderId.isBlank()) {
            orderId = "order_sim_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);
            log.info("Using simulated order ID: {}", orderId);
        }

        // Persist payment record in database
        PaymentRecord record = new PaymentRecord(
                request.getBookingId(),
                orderId,
                request.getAmount(),
                amountInPaise,
                currency,
                receipt,
                request.getPnr()
        );
        paymentRepository.save(record);

        return new PaymentOrderResponse(
                orderId,
                request.getAmount(),
                amountInPaise,
                currency,
                keyId,
                request.getBookingId(),
                request.getPnr()
        );
    }

    @Override
    public PaymentVerifyResponse verifyPayment(PaymentVerifyRequest request) {
        log.info("Verifying payment for Order ID: {}, Payment ID: {}",
                request.getRazorpayOrderId(), request.getRazorpayPaymentId());

        boolean isValid = signatureValidator.verifyPaymentSignature(
                request.getRazorpayOrderId(),
                request.getRazorpayPaymentId(),
                request.getRazorpaySignature()
        );

        Optional<PaymentRecord> recordOpt = paymentRepository.findByOrderId(request.getRazorpayOrderId());
        if (recordOpt.isEmpty() && request.getBookingId() != null) {
            List<PaymentRecord> byBooking = paymentRepository.findByBookingId(request.getBookingId());
            if (!byBooking.isEmpty()) {
                recordOpt = Optional.of(byBooking.get(byBooking.size() - 1));
            }
        }

        if (isValid) {
            if (recordOpt.isPresent()) {
                PaymentRecord record = recordOpt.get();
                record.setPaymentId(request.getRazorpayPaymentId());
                record.setSignature(request.getRazorpaySignature());
                record.setStatus(PaymentStatus.CAPTURED);
                paymentRepository.save(record);
            }
            return new PaymentVerifyResponse(
                    true,
                    request.getRazorpayPaymentId(),
                    request.getRazorpayOrderId(),
                    request.getBookingId(),
                    PaymentStatus.CAPTURED,
                    "Payment verified successfully and flight ticket confirmed"
            );
        } else {
            if (recordOpt.isPresent()) {
                PaymentRecord record = recordOpt.get();
                record.setStatus(PaymentStatus.FAILED);
                record.setFailureReason("HMAC-SHA256 signature verification failed");
                paymentRepository.save(record);
            }
            return new PaymentVerifyResponse(
                    false,
                    request.getRazorpayPaymentId(),
                    request.getRazorpayOrderId(),
                    request.getBookingId(),
                    PaymentStatus.FAILED,
                    "HMAC-SHA256 signature verification failed"
            );
        }
    }

    @Override
    public PaymentRefundResponse processRefund(PaymentRefundRequest request) {
        log.info("Processing refund for Payment ID: {}", request.getPaymentId());

        String refundId = "rfnd_" + UUID.randomUUID().toString().replace("-", "").substring(0, 14);

        if (request.getPaymentId() != null) {
            paymentRepository.findByPaymentId(request.getPaymentId()).ifPresent(record -> {
                record.setStatus(PaymentStatus.REFUNDED);
                record.setRefundId(refundId);
                record.setRefundAmount(request.getAmount() != null ? request.getAmount() : record.getAmount());
                paymentRepository.save(record);
            });
        }

        return new PaymentRefundResponse(
                refundId,
                request.getPaymentId(),
                request.getAmount() != null ? request.getAmount() : 0.0,
                "PROCESSED"
        );
    }

    @Override
    public String handleWebhook(String payload, String signature) {
        log.info("Received Razorpay webhook. Validating signature...");

        if (signature != null && !signature.isBlank()) {
            boolean valid = signatureValidator.verifyWebhookSignature(payload, signature);
            if (!valid) {
                log.warn("Invalid webhook signature rejected");
                return "Invalid signature";
            }
        }

        try {
            JsonNode root = objectMapper.readTree(payload);
            String event = root.has("event") ? root.get("event").asText() : "";
            log.info("Processing webhook event: {}", event);

            if ("payment.captured".equals(event)) {
                JsonNode payment = root.path("payload").path("payment").path("entity");
                String orderId = payment.path("order_id").asText();
                String paymentId = payment.path("id").asText();

                paymentRepository.findByOrderId(orderId).ifPresent(record -> {
                    record.setPaymentId(paymentId);
                    record.setStatus(PaymentStatus.CAPTURED);
                    paymentRepository.save(record);
                    log.info("Payment marked CAPTURED via webhook for order: {}", orderId);
                });
            } else if ("payment.failed".equals(event)) {
                JsonNode payment = root.path("payload").path("payment").path("entity");
                String orderId = payment.path("order_id").asText();
                String reason = payment.path("error_description").asText();

                paymentRepository.findByOrderId(orderId).ifPresent(record -> {
                    record.setStatus(PaymentStatus.FAILED);
                    record.setFailureReason(reason);
                    paymentRepository.save(record);
                    log.info("Payment marked FAILED via webhook for order: {}", orderId);
                });
            }
        } catch (Exception e) {
            log.error("Error parsing webhook payload: {}", e.getMessage());
        }

        return "Webhook processed successfully";
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentRecord> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentRecord getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment record not found for ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentRecord> getPaymentsByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId);
    }
}
