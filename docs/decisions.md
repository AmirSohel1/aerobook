# AeroBook Architecture & Engineering Decisions Log (ADR)

---

## ADR-001: Centralized API Ingress Through API Gateway Only
- **Context**: The frontend was making scattered requests, and some mock implementations simulated direct calls.
- **Decision**: All frontend requests strictly target the central Spring Cloud API Gateway at `http://localhost:8083` (configured in `environment.ts`). No microservice exposes public ports directly to the browser.
- **Consequences**: Consistent JWT verification, unified CORS handling, and single point of observability.

---

## ADR-002: Zero Mock Fallbacks Across Frontend Portals
- **Context**: Several frontend components fell back to static hardcoded arrays (`seedFallbackBookings`, `seedFallbackFlights`, fake setTimeout) when network errors occurred.
- **Decision**: Remove 100% of mock fallbacks. Standardize on a 3-state component pattern:
  - **Loading State**: Reusable skeleton components (`ab-skeleton`).
  - **Error State**: Actionable error banner with retry handlers (`ab-error-state`).
  - **Empty State**: Friendly domain-specific empty representations (`ab-empty-state`).
- **Enforcement**: Automated verification script `npm run verify:no-mocks` executed during build pipeline.

---

## ADR-003: Fare Service Extension for Coupon Lifecycle
- **Context**: Passenger checkout and Admin dashboard needed dynamic coupon validation and coupon management.
- **Decision**: Extend `fare-service` (Port 8089) with a JPA entity `Coupon.java`, repository, validation service with percentage and slab calculations, and REST controllers (`/api/fares/coupons`).
- **Consequences**: Business discounts are strictly governed server-side rather than calculated in client JavaScript.

---

## ADR-004: User Service Extension for Operational Incident Reporting
- **Context**: Admin and Staff dashboards needed to log and monitor security incidents and baggage/gate escalations.
- **Decision**: Extend `user-service` (Port 8084) with `IncidentReport.java`, `IncidentSeverity`, and `IncidentStatus` enums, storing records in `aerobook_user_db`.
- **Consequences**: Ground staff can file incidents and Admin can audit or resolve them through persistent storage.

---

## ADR-005: Dedicated Microservice for Razorpay Payments (`payment-service`)
- **Context**: Flight booking requires an industry-standard, secure payment workflow using Razorpay test mode.
- **Decision**: Build a standalone Spring Boot microservice `payment-service` (Port 8092, Eureka registered, MySQL `aerobook_payment_db`).
- **Security**:
  - `RAZORPAY_KEY_SECRET` and `RAZORPAY_WEBHOOK_SECRET` stay exclusively on the backend.
  - Server calculates HMAC-SHA256 hash using Java's standard cryptographic provider and validates `orderId + "|" + paymentId` against the client signature.
  - Test mode / simulated fallback token support for CI/CD test runs.

---

## ADR-006: Responsive HTML Digital Boarding Pass Email Templates
- **Context**: Confirmation and boarding notices needed professional email delivery matching AeroBook's design tokens.
- **Decision**: Equip `notification-service` with `spring-boot-starter-mail` and a dedicated `EmailService` generating table-based, mobile-responsive HTML emails themed with `#635BFF`.
- **Triggers**: Booking confirmation, mobile check-in pass issuance, schedule delays, and refund receipts.
