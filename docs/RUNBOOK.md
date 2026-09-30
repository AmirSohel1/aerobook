# AeroBook Platform Runbook & Developer Guide

This guide provides instructions for configuring, launching, and verifying the **AeroBook** full-stack cloud airline platform.

---

## 1. Prerequisites

- **Java Development Kit (JDK)**: Version 17 or higher
- **Node.js**: Version 18.x or 20.x with `npm`
- **MySQL Server**: Version 8.0 or higher
- **Maven**: Maven wrapper (`mvnw.cmd` on Windows, `./mvnw` on Linux/macOS) is included in each service

---

## 2. Environment Configuration

Copy the root template `.env.example` to `.env`:

```bash
cp .env.example .env
```

### Essential Secrets:

1. **JWT Secret (`JWT_SECRET`)**: Base64 encoded key used by `auth-service` and validated by `api-gateway`.
2. **Razorpay Credentials (`RAZORPAY_KEY_ID`, `RAZORPAY_KEY_SECRET`, `RAZORPAY_WEBHOOK_SECRET`)**:
   - Create a free test account at [https://dashboard.razorpay.com](https://dashboard.razorpay.com).
   - Generate test API Keys under **Settings &rarr; API Keys**.
   - Set `RAZORPAY_KEY_ID` (starts with `rzp_test_...`) and `RAZORPAY_KEY_SECRET`.
   - Set `RAZORPAY_WEBHOOK_SECRET` for webhook signature verification.
3. **SMTP Email Credentials (`SPRING_MAIL_USERNAME`, `SPRING_MAIL_PASSWORD`)**:
   - For Gmail, generate an App Password under **Google Account &rarr; Security &rarr; 2-Step Verification &rarr; App Passwords**.

---

## 3. Database Initialization

Ensure MySQL is running on port 3306. The microservices are configured with `createDatabaseIfNotExist=true` in their JDBC URLs:

```sql
CREATE DATABASE IF NOT EXISTS aerobook_auth_db;
CREATE DATABASE IF NOT EXISTS aerobook_user_db;
CREATE DATABASE IF NOT EXISTS aerobook_flight_db;
CREATE DATABASE IF NOT EXISTS aerobook_booking_db;
CREATE DATABASE IF NOT EXISTS aerobook_fare_db;
CREATE DATABASE IF NOT EXISTS aerobook_checkin_db;
CREATE DATABASE IF NOT EXISTS aerobook_notification_db;
CREATE DATABASE IF NOT EXISTS aerobook_payment_db;
```

Hibernate/JPA `ddl-auto=update` automatically creates the tables, foreign keys, and indexes on initial startup.

---

## 4. Service Startup Sequence

Start services in the following order to ensure discovery and routing dependencies resolve cleanly:

### Step 1: Service Registry (Eureka)

```bash
cd eureka-service
./mvnw spring-boot:run
# Windows: cmd /c "mvnw.cmd spring-boot:run"
# UI available at: http://localhost:8761
```

### Step 2: API Gateway

```bash
cd api-gateway
./mvnw spring-boot:run
# Port: 8083 (Swagger UI: http://localhost:8083/swagger-ui.html)
```

### Step 3: Core Domain Microservices

Launch each in a separate terminal:

```bash
# 1. Auth Service (Port 8082)
cd auth-service && ./mvnw spring-boot:run

# 2. User Service (Port 8084)
cd user-service && ./mvnw spring-boot:run

# 3. Flight Service (Port 8087)
cd flight-service && ./mvnw spring-boot:run

# 4. Booking Service (Port 8088)
cd booking-service && ./mvnw spring-boot:run

# 5. Fare Service (Port 8089)
cd fare-service && ./mvnw spring-boot:run

# 6. Check-In Service (Port 8090)
cd check-in-service && ./mvnw spring-boot:run

# 7. Notification Service (Port 8091)
cd notification-service && ./mvnw spring-boot:run

# 8. Payment Service (Port 8092)
cd payment-service && ./mvnw spring-boot:run
```

### Step 4: Angular Frontend

```bash
cd frontend
npm install
npm start
# Port: 4200 (Accessible at: http://localhost:4200)
```

---

## 5. Verification & Testing

### 5.1 Automated Mock-Free Verification

Verify that 0 mock, dummy, or fake data patterns exist in the frontend features:

```bash
cd frontend
npm run verify:no-mocks
```

**Expected Output**:

```
🔍 Scanning frontend/src/app/features for mock/dummy/fake patterns...
✅ Clean: No mock data patterns found in features! All data is backed by real services.
```

### 5.2 Angular Production Build

Verify that all TypeScript types, Angular components, and HTML templates compile without errors:

```bash
cd frontend
npm run build
```

**Expected Output**:

```
Application bundle generation complete.
Output location: dist/frontend
```

### 5.3 Backend Compilation Check

Run the compiler across backend services to verify complete contract alignment:

```bash
# Example for payment service:
cd payment-service && ./mvnw compile -DskipTests
```

---

## 6. Default Seed Accounts & Coupons

### Roles & Test Accounts:

- **Admin**: `admin@aerobook.com` / `Admin@123`
- **Staff**: `staff@aerobook.com` / `Staff@123`
- **Passenger**: `priya@gmail.com` / `Passenger@123`

### Active Seeded Coupons (`fare-service`):

- `AERO2026`: 20% discount (Max ₹1,500)
- `FLYHIGH20`: 20% discount (Max ₹1,000)
- `GOLD15`: 15% discount (Max ₹750)
- `SPECIAL15`: 15% discount (Max ₹500)

---

## 7. Troubleshooting

| Issue                                   | Cause                                         | Solution                                                                                 |
| --------------------------------------- | --------------------------------------------- | ---------------------------------------------------------------------------------------- |
| `CORS Error` on frontend                | Direct service port called instead of Gateway | Ensure frontend calls only Gateway port `8083` via `environment.ts`.                     |
| Razorpay checkout modal does not pop up | Missing checkout SDK or browser popup blocked | `index.html` loads Razorpay SDK; check browser console for popup blocker.                |
| `Signature verification failed`         | Mismatched secret or order payload            | Ensure `RAZORPAY_KEY_SECRET` in `.env` matches your Razorpay dashboard secret.           |
| Notification email not sending          | SMTP credentials invalid or STARTTLS blocked  | Verify Gmail App Password and port 587 in `notification-service/application.properties`. |
