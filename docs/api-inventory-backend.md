# Backend API Endpoint Inventory

This document provides a comprehensive catalog of every backend microservice endpoint across the AeroBook ecosystem, detailing service port, HTTP method, route path, role authorizations, request payload DTOs with validation rules, response DTOs, and error definitions.

---

## 1. Auth Service (`AUTH-SERVICE` - Port 8082, MySQL: `aerobook_auth_db`)
**Base Gateway Route**: `/api/auth/**`

### 1.1 `GET /api/auth/health`
- **Roles Allowed**: Public
- **Request**: None
- **Response**: `200 OK`
  - Body: `{ "service": "auth-service", "status": "UP", "port": 8082, "timestamp": string }`
- **Validation**: None

### 1.2 `POST /api/auth/register`
- **Roles Allowed**: Public
- **Request DTO**: `RegisterRequest`
  - `firstName`: `String` (Required, `@NotBlank`, max 50)
  - `lastName`: `String` (Required, `@NotBlank`, max 50)
  - `email`: `String` (Required, `@NotBlank`, `@Email`)
  - `phoneNumber`: `String` (Optional, `@Pattern(^[0-9]{10,15}$)`)
  - `dateOfBirth`: `LocalDate` (Optional, `YYYY-MM-DD`)
  - `nationality`: `String` (Optional, max 50)
  - `password`: `String` (Required, `@NotBlank`, min 6)
- **Response**: `201 Created` &rarr; `AuthResponse`
  - `accessToken`: `String` (JWT token, 1 hr validity)
  - `refreshToken`: `String` (UUID token, 7 days validity)
  - `tokenType`: `"Bearer"`
  - `role`: `"ROLE_USER"`
  - `userId`: `Long`
- **Errors**: `400 Bad Request` (Validation errors), `409 Conflict` (Email already exists)

### 1.3 `POST /api/auth/register-staff`
- **Roles Allowed**: Public with secret header `X-Staff-Setup-Key: AerobookStaffSetup2026`
- **Request DTO**: `RegisterRequest` (same validation as above)
- **Response**: `201 Created` &rarr; `AuthResponse` (with `role: "ROLE_STAFF"`)
- **Errors**: `400 Bad Request`, `403 Forbidden` (Invalid/missing setup key), `409 Conflict`

### 1.4 `POST /api/auth/register-admin`
- **Roles Allowed**: Public with secret header `X-Admin-Setup-Key: AerobookAdminSetup2026`
- **Request DTO**: `RegisterRequest` (same validation as above)
- **Response**: `201 Created` &rarr; `AuthResponse` (with `role: "ROLE_ADMIN"`)
- **Errors**: `400 Bad Request`, `403 Forbidden` (Invalid/missing setup key), `409 Conflict`

### 1.5 `POST /api/auth/login`
- **Roles Allowed**: Public
- **Request DTO**: `LoginRequest`
  - `email`: `String` (Required, `@NotBlank`, `@Email`)
  - `password`: `String` (Required, `@NotBlank`)
- **Response**: `200 OK` &rarr; `AuthResponse`
  - `accessToken`: `String`
  - `refreshToken`: `String`
  - `tokenType`: `"Bearer"`
  - `role`: `String` (`ROLE_USER` | `ROLE_STAFF` | `ROLE_ADMIN`)
  - `userId`: `Long`
- **Errors**: `400 Bad Request`, `401 Unauthorized` (Invalid credentials)

### 1.6 `POST /api/auth/refresh-token`
- **Roles Allowed**: Public
- **Request DTO**: `RefreshTokenRequest`
  - `refreshToken`: `String` (Required, `@NotBlank`)
- **Response**: `200 OK` &rarr; `AuthResponse`
- **Errors**: `400 Bad Request`, `401 Unauthorized` (Expired or revoked token), `404 Not Found`

### 1.7 `GET /api/auth/credentials`
- **Roles Allowed**: `ROLE_ADMIN`
- **Request**: None
- **Response**: `200 OK` &rarr; `List<CredentialResponse>`
  - `id`: `Long`
  - `email`: `String`
  - `role`: `String`
  - `createdAt`: `LocalDateTime`
  - `lastLogin`: `LocalDateTime`
- **Errors**: `401 Unauthorized`, `403 Forbidden`

### 1.8 `PUT /api/auth/credentials/{userId}/role`
- **Roles Allowed**: `ROLE_ADMIN`
- **Parameters**: `userId: Long` (path), `role: String` (query: `ROLE_USER` | `ROLE_STAFF` | `ROLE_ADMIN`)
- **Response**: `200 OK` &rarr; `CredentialResponse`
- **Errors**: `400 Bad Request` (Invalid role name), `401 Unauthorized`, `403 Forbidden`, `404 Not Found`

---

## 2. User Service (`USER-SERVICE` - Port 8084, MySQL: `aerobook_user_db`)
**Base Gateway Route**: `/api/v1/users/**`

### 2.1 `GET /api/v1/users/health`
- **Roles Allowed**: Public
- **Request**: None
- **Response**: `200 OK` &rarr; `"User Service is up and running!"`

### 2.2 `POST /api/v1/users`
- **Roles Allowed**: `ROLE_ADMIN`
- **Request DTO**: `UserRequest`
  - `firstName`: `String` (Required, `@NotBlank`)
  - `lastName`: `String` (Required, `@NotBlank`)
  - `email`: `String` (Required, `@NotBlank`, `@Email`)
  - `phoneNumber`: `String` (Required, `@NotBlank`, `@Pattern(^[0-9]{10,15}$)`)
  - `dateOfBirth`: `LocalDate` (Optional, `YYYY-MM-DD`)
  - `nationality`: `String` (Optional)
  - `role`: `String` (Optional, defaults to `ROLE_USER`)
- **Response**: `201 Created` &rarr; `UserResponse`
  - `userId`: `Long`
  - `firstName`: `String`, `lastName`: `String`, `email`: `String`, `phoneNumber`: `String`
  - `dateOfBirth`: `LocalDate`, `nationality`: `String`, `role`: `String`
- **Errors**: `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `409 Conflict`

### 2.3 `GET /api/v1/users`
- **Roles Allowed**: `ROLE_ADMIN`
- **Request**: None
- **Response**: `200 OK` &rarr; `List<UserResponse>`
- **Errors**: `401 Unauthorized`, `403 Forbidden`

### 2.4 `GET /api/v1/users/{id}`
- **Roles Allowed**: `ROLE_USER`, `ROLE_ADMIN` (Owner or Admin)
- **Parameters**: `id: Long` (path)
- **Response**: `200 OK` &rarr; `UserResponse`
- **Errors**: `401 Unauthorized`, `404 Not Found`

### 2.5 `GET /api/v1/users/email/{email}`
- **Roles Allowed**: `ROLE_USER`, `ROLE_ADMIN`
- **Parameters**: `email: String` (path)
- **Response**: `200 OK` &rarr; `UserResponse`
- **Errors**: `401 Unauthorized`, `404 Not Found`

### 2.6 `PUT /api/v1/users/{id}`
- **Roles Allowed**: `ROLE_USER`, `ROLE_ADMIN`
- **Parameters**: `id: Long` (path)
- **Request DTO**: `UserRequest` (same validation as 2.2)
- **Response**: `200 OK` &rarr; `UserResponse`
- **Errors**: `400 Bad Request`, `401 Unauthorized`, `404 Not Found`

### 2.7 `DELETE /api/v1/users/{id}`
- **Roles Allowed**: `ROLE_ADMIN`
- **Parameters**: `id: Long` (path)
- **Response**: `200 OK` &rarr; `"User account deleted successfully with ID: {id}"`
- **Errors**: `401 Unauthorized`, `403 Forbidden`, `404 Not Found`

---

## 3. Flight Service (`FLIGHT-SERVICE` - Port 8087, MySQL: `aerobook_flight_db`)
**Base Gateway Route**: `/api/flights/**`, `/api/admin/flights/**`, `/api/admin/aircrafts/**`

### 3.1 `GET /api/flights/health`
- **Roles Allowed**: Public
- **Response**: `200 OK` &rarr; `"Flight Service is up and running!"`

### 3.2 `GET /api/flights/search`
- **Roles Allowed**: `ROLE_USER`, `ROLE_STAFF`, `ROLE_ADMIN`, Public
- **Query Parameters**: `source: String` (Required), `destination: String` (Required)
- **Response**: `200 OK` &rarr; `List<FlightResponse>`
  - `id`: `Long`
  - `flightNumber`: `String`
  - `airlineName`: `String`
  - `source`: `String`, `destination`: `String`
  - `departureTime`: `LocalDateTime`, `arrivalTime`: `LocalDateTime`
  - `totalSeats`: `Integer`, `availableSeats`: `Integer`
  - `baseFare`: `BigDecimal`
  - `status`: `String` (`SCHEDULED`, `BOARDING`, `DELAYED`, `DEPARTED`, `COMPLETED`, `CANCELLED`)

### 3.3 `GET /api/flights`
- **Roles Allowed**: `ROLE_USER`, `ROLE_STAFF`, `ROLE_ADMIN`, Public
- **Response**: `200 OK` &rarr; `List<FlightResponse>`

### 3.4 `GET /api/flights/{id}`
- **Roles Allowed**: `ROLE_USER`, `ROLE_STAFF`, `ROLE_ADMIN`, Public
- **Parameters**: `id: Long` (path)
- **Response**: `200 OK` &rarr; `FlightResponse`
- **Errors**: `404 Not Found`

### 3.5 `PUT /api/flights/{id}/status`
- **Roles Allowed**: `ROLE_STAFF`, `ROLE_ADMIN`
- **Parameters**: `id: Long` (path), `status: String` (query: `BOARDING`, `DELAYED`, `DEPARTED`, `COMPLETED`, `CANCELLED`)
- **Response**: `200 OK` &rarr; `FlightResponse`
- **Errors**: `400 Bad Request` (Invalid status), `403 Forbidden`, `404 Not Found`

### 3.6 `POST /api/admin/flights`
- **Roles Allowed**: `ROLE_ADMIN`
- **Request DTO**: `CreateFlightRequest`
  - `flightNumber`: `String` (Required, `@NotBlank`)
  - `airlineName`: `String` (Required, `@NotBlank`)
  - `source`: `String` (Required, `@NotBlank`)
  - `destination`: `String` (Required, `@NotBlank`)
  - `departureTime`: `LocalDateTime` (Required, `@NotNull`)
  - `arrivalTime`: `LocalDateTime` (Required, `@NotNull`)
  - `totalSeats`: `Integer` (Required, `@NotNull`)
  - `baseFare`: `BigDecimal` (Required, `@NotNull`)
  - `aircraftId`: `Long` (Required, `@NotNull`)
- **Response**: `201 Created` &rarr; `FlightResponse`
- **Errors**: `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found` (Aircraft not found)

### 3.7 `PUT /api/admin/flights/{id}`
- **Roles Allowed**: `ROLE_ADMIN`
- **Parameters**: `id: Long` (path)
- **Request DTO**: `UpdateFlightRequest`
  - `airlineName`: `String`, `source`: `String`, `destination`: `String`
  - `departureTime`: `LocalDateTime`, `arrivalTime`: `LocalDateTime`
  - `totalSeats`: `Integer`, `baseFare`: `BigDecimal`
- **Response**: `200 OK` &rarr; `FlightResponse`
- **Errors**: `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found`

### 3.8 `DELETE /api/admin/flights/{id}`
- **Roles Allowed**: `ROLE_ADMIN`
- **Parameters**: `id: Long` (path)
- **Response**: `200 OK` &rarr; `"Flight schedule deleted successfully with ID: {id}"`
- **Errors**: `401 Unauthorized`, `403 Forbidden`, `404 Not Found`

### 3.9 `GET /api/admin/flights`
- **Roles Allowed**: `ROLE_ADMIN`
- **Response**: `200 OK` &rarr; `List<FlightResponse>`
- **Errors**: `401 Unauthorized`, `403 Forbidden`

### 3.10 `POST /api/admin/aircrafts`
- **Roles Allowed**: `ROLE_ADMIN`
- **Request DTO**: `CreateAircraftRequest`
  - `aircraftCode`: `String` (Required, `@NotBlank`)
  - `aircraftName`: `String` (Required, `@NotBlank`)
  - `manufacturer`: `String` (Required, `@NotBlank`)
  - `capacity`: `Integer` (Required, `@NotNull`, `@Min(1)`)
- **Response**: `201 Created` &rarr; `Aircraft`
  - `id`: `Long`, `aircraftCode`: `String`, `aircraftName`: `String`, `manufacturer`: `String`
  - `capacity`: `Integer`, `status`: `AircraftStatus`, `createdAt`: `LocalDateTime`
- **Errors**: `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`

### 3.11 `GET /api/admin/aircrafts`
- **Roles Allowed**: `ROLE_ADMIN`
- **Response**: `200 OK` &rarr; `List<Aircraft>`
- **Errors**: `401 Unauthorized`, `403 Forbidden`

### 3.12 `GET /api/admin/aircrafts/{id}`
- **Roles Allowed**: `ROLE_ADMIN`
- **Parameters**: `id: Long` (path)
- **Response**: `200 OK` &rarr; `Aircraft`
- **Errors**: `401 Unauthorized`, `403 Forbidden`, `404 Not Found`

### 3.13 `DELETE /api/admin/aircrafts/{id}`
- **Roles Allowed**: `ROLE_ADMIN`
- **Parameters**: `id: Long` (path)
- **Response**: `200 OK` &rarr; `"Aircraft deleted successfully with ID: {id}"`
- **Errors**: `401 Unauthorized`, `403 Forbidden`, `404 Not Found`

---

## 4. Fare Service (`FARE-SERVICE` - Port 8089, MySQL: `aerobook_fare_db`)
**Base Gateway Route**: `/api/fares/**`

### 4.1 `GET /api/fares/health`
- **Roles Allowed**: Public
- **Response**: `200 OK` &rarr; `{ "service": "fare-service", "status": "UP", "port": 8089 }`

### 4.2 `POST /api/fares`
- **Roles Allowed**: `ROLE_ADMIN`
- **Request DTO**: `FareRequest`
  - `flightId`: `Long` (Required, `@NotNull`)
  - `economyFare`: `BigDecimal` (Required, `@NotNull`, `@DecimalMin("0.0")`)
  - `businessFare`: `BigDecimal` (Required, `@NotNull`, `@DecimalMin("0.0")`)
  - `firstClassFare`: `BigDecimal` (Required, `@NotNull`, `@DecimalMin("0.0")`)
  - `taxPercentage`: `BigDecimal` (Required, `@NotNull`, `@DecimalMin("0.0")`, `@DecimalMax("100.0")`)
  - `discountPercentage`: `BigDecimal` (Optional, `@DecimalMin("0.0")`, `@DecimalMax("100.0")`)
  - `effectiveDate`: `LocalDate` (Optional)
- **Response**: `201 Created` &rarr; `FareResponse`
  - `id`: `Long`, `flightId`: `Long`
  - `economyFare`: `BigDecimal`, `businessFare`: `BigDecimal`, `firstClassFare`: `BigDecimal`
  - `taxPercentage`: `BigDecimal`, `discountPercentage`: `BigDecimal`
  - `effectiveDate`: `LocalDate`, `active`: `Boolean`
- **Errors**: `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`

### 4.3 `GET /api/fares/{id}`
- **Roles Allowed**: `ROLE_USER`, `ROLE_ADMIN`
- **Parameters**: `id: Long` (path)
- **Response**: `200 OK` &rarr; `FareResponse`
- **Errors**: `404 Not Found`

### 4.4 `GET /api/fares/flight/{flightId}`
- **Roles Allowed**: `ROLE_USER`, `ROLE_ADMIN`
- **Parameters**: `flightId: Long` (path)
- **Response**: `200 OK` &rarr; `FareResponse`
- **Errors**: `404 Not Found`

### 4.5 `GET /api/fares`
- **Roles Allowed**: `ROLE_USER`, `ROLE_ADMIN`
- **Response**: `200 OK` &rarr; `List<FareResponse>`

### 4.6 `PUT /api/fares/{id}`
- **Roles Allowed**: `ROLE_ADMIN`
- **Parameters**: `id: Long` (path)
- **Request DTO**: `FareRequest` (same validation as 4.2)
- **Response**: `200 OK` &rarr; `FareResponse`
- **Errors**: `400 Bad Request`, `401 Unauthorized`, `403 Forbidden`, `404 Not Found`

### 4.7 `DELETE /api/fares/{id}`
- **Roles Allowed**: `ROLE_ADMIN`
- **Parameters**: `id: Long` (path)
- **Response**: `200 OK` &rarr; `"Fare deleted successfully with ID: {id}"`
- **Errors**: `401 Unauthorized`, `403 Forbidden`, `404 Not Found`

---

## 5. Booking Service (`BOOKING-SERVICE` - Port 8088, MySQL: `aerobook_booking_db`)
**Base Gateway Route**: `/api/bookings/**`

### 5.1 `GET /api/bookings/health`
- **Roles Allowed**: Public
- **Response**: `200 OK` &rarr; `{ "service": "booking-service", "status": "UP", "port": 8088 }`

### 5.2 `POST /api/bookings`
- **Roles Allowed**: `ROLE_USER`, `ROLE_ADMIN`
- **Request DTO**: `BookingRequest`
  - `userId`: `Long` (Required, `@NotNull`)
  - `flightId`: `Long` (Required, `@NotNull`)
  - `passengers`: `List<PassengerRequest>` (Required, `@NotEmpty`, `@Valid`)
    - `firstName`: `String` (Required, `@NotBlank`)
    - `lastName`: `String` (Required, `@NotBlank`)
    - `age`: `Integer` (Required, `@NotNull`, `@Min(1)`, `@Max(120)`)
    - `gender`: `String` (Required, `@NotBlank`, `@Pattern(^(?i)[MFO]$)`)
- **Response**: `200 OK` &rarr; `BookingResponse`
  - `bookingId`: `Long`, `pnr`: `String` (e.g. `PNR-AB1001`)
  - `userId`: `Long`, `flightId`: `Long`
  - `flightNumber`: `String`, `airlineName`: `String`
  - `source`: `String`, `destination`: `String`
  - `departureTime`: `LocalDateTime`, `arrivalTime`: `LocalDateTime`, `bookingDate`: `LocalDateTime`
  - `totalFare`: `BigDecimal` (computed server-side from baseFare * passenger count)
  - `status`: `String` (`BOOKED`, `CANCELLED`)
  - `passengers`: `List<PassengerResponse>`
- **Errors**: `400 Bad Request`, `401 Unauthorized`, `404 Not Found` (Flight not found)

### 5.3 `GET /api/bookings/{bookingId}`
- **Roles Allowed**: `ROLE_USER`, `ROLE_ADMIN`
- **Parameters**: `bookingId: Long` (path)
- **Response**: `200 OK` &rarr; `BookingResponse`
- **Errors**: `404 Not Found`

### 5.4 `GET /api/bookings/pnr/{pnr}`
- **Roles Allowed**: `ROLE_USER`, `ROLE_STAFF`, `ROLE_ADMIN`
- **Parameters**: `pnr: String` (path)
- **Response**: `200 OK` &rarr; `BookingResponse`
- **Errors**: `404 Not Found`

### 5.5 `GET /api/bookings/user/{userId}`
- **Roles Allowed**: `ROLE_USER`, `ROLE_ADMIN`
- **Parameters**: `userId: Long` (path)
- **Response**: `200 OK` &rarr; `List<BookingResponse>`

### 5.6 `GET /api/bookings/flight/{flightId}`
- **Roles Allowed**: `ROLE_STAFF`, `ROLE_ADMIN`
- **Parameters**: `flightId: Long` (path)
- **Response**: `200 OK` &rarr; `List<BookingResponse>` (Flight Passenger Manifest)
- **Errors**: `403 Forbidden`

### 5.7 `GET /api/bookings`
- **Roles Allowed**: `ROLE_STAFF`, `ROLE_ADMIN`
- **Response**: `200 OK` &rarr; `List<BookingResponse>`
- **Errors**: `403 Forbidden`

### 5.8 `DELETE /api/bookings/{bookingId}`
- **Roles Allowed**: `ROLE_USER`, `ROLE_ADMIN`
- **Parameters**: `bookingId: Long` (path)
- **Response**: `200 OK` &rarr; `"Booking cancelled successfully with ID: {bookingId}"`
- **Errors**: `400 Bad Request` (Already cancelled), `404 Not Found`

---

## 6. Check-In Service (`CHECK-IN-SERVICE` - Port 8090, MySQL: `aerobook_checkin_db`)
**Base Gateway Route**: `/api/check-ins/**`

### 6.1 `GET /api/check-ins/health`
- **Roles Allowed**: Public
- **Response**: `200 OK` &rarr; `{ "service": "check-in-service", "status": "UP", "port": 8090 }`

### 6.2 `POST /api/check-ins`
- **Roles Allowed**: `ROLE_USER`, `ROLE_STAFF`, `ROLE_ADMIN`
- **Request DTO**: `CheckInRequest`
  - `bookingId`: `Long` (Required, `@NotNull`)
  - `passengerName`: `String` (Required, `@NotBlank`, `@Size(min=2, max=100)`)
  - `seatNumber`: `String` (Optional, assigned automatically if omitted)
- **Response**: `200 OK` &rarr; `CheckIn`
  - `id`: `Long`, `bookingId`: `Long`
  - `passengerName`: `String`, `seatNumber`: `String`
  - `boardingPassNumber`: `String` (format: `BP-{bookingId}-{seatNumber}`)
  - `status`: `"CHECKED_IN"`, `checkedInAt`: `LocalDateTime`
- **Errors**: `400 Bad Request`, `401 Unauthorized`, `404 Not Found` (Booking not found)

### 6.3 `GET /api/check-ins/booking/{bookingId}`
- **Roles Allowed**: `ROLE_USER`, `ROLE_STAFF`, `ROLE_ADMIN`
- **Parameters**: `bookingId: Long` (path)
- **Response**: `200 OK` &rarr; `CheckIn`
- **Errors**: `404 Not Found`

### 6.4 `GET /api/check-ins/{id}`
- **Roles Allowed**: `ROLE_USER`, `ROLE_ADMIN`
- **Parameters**: `id: Long` (path)
- **Response**: `200 OK` &rarr; `CheckIn`
- **Errors**: `404 Not Found`

### 6.5 `GET /api/check-ins`
- **Roles Allowed**: `ROLE_STAFF`, `ROLE_ADMIN`
- **Response**: `200 OK` &rarr; `List<CheckIn>`
- **Errors**: `403 Forbidden`

### 6.6 `POST /api/check-ins/verify`
- **Roles Allowed**: `ROLE_STAFF`, `ROLE_ADMIN`
- **Parameters**: `boardingPassNumber: String` (query)
- **Response**: `200 OK` &rarr; `CheckIn` (with `status: "BOARDED"`)
- **Errors**: `403 Forbidden`, `404 Not Found`

---

## 7. Notification Service (`NOTIFICATION-SERVICE` - Port 8091, MySQL: `aerobook_notification_db`)
**Base Gateway Route**: `/api/notifications/**`

### 7.1 `GET /api/notifications/health`
- **Roles Allowed**: Public
- **Response**: `200 OK` &rarr; `{ "service": "notification-service", "status": "UP", "port": 8091 }`

### 7.2 `POST /api/notifications/send`
- **Roles Allowed**: Authenticated services / Admin
- **Request DTO**: `NotificationRequest`
  - `userId`: `Long` (Optional)
  - `recipientEmail`: `String` (Optional)
  - `title`: `String` (Required, `@NotBlank`)
  - `message`: `String` (Required, `@NotBlank`)
  - `type`: `NotificationType` (`INFO`, `BOOKING_CONFIRMED`, `CHECKIN_SUCCESS`, `FLIGHT_DELAY`, `GATE_CHANGE`, `BOARDING_CALL`, `BROADCAST`)
  - `flightNumber`: `String` (Optional)
- **Response**: `201 Created` &rarr; `NotificationResponse`
  - `id`: `Long`, `userId`: `Long`, `recipientEmail`: `String`
  - `title`: `String`, `message`: `String`, `type`: `String`, `status`: `String`
  - `flightNumber`: `String`, `createdAt`: `LocalDateTime`, `readAt`: `LocalDateTime`

### 7.3 `POST /api/notifications/broadcast`
- **Roles Allowed**: `ROLE_STAFF`, `ROLE_ADMIN`
- **Request DTO**: `BroadcastRequest`
  - `title`: `String` (Required, `@NotBlank`)
  - `message`: `String` (Required, `@NotBlank`)
  - `type`: `NotificationType`
  - `flightNumber`: `String` (Optional)
- **Response**: `201 Created` &rarr; `NotificationResponse`
- **Errors**: `403 Forbidden`

### 7.4 `GET /api/notifications/user/{userId}`
- **Roles Allowed**: `ROLE_USER`, `ROLE_ADMIN`
- **Parameters**: `userId: Long` (path)
- **Response**: `200 OK` &rarr; `List<NotificationResponse>`

### 7.5 `GET /api/notifications/unread-count/{userId}`
- **Roles Allowed**: `ROLE_USER`, `ROLE_ADMIN`
- **Parameters**: `userId: Long` (path)
- **Response**: `200 OK` &rarr; `{ "userId": Long, "unreadCount": long }`

### 7.6 `PUT /api/notifications/{id}/read`
- **Roles Allowed**: `ROLE_USER`, `ROLE_ADMIN`
- **Parameters**: `id: Long` (path)
- **Response**: `200 OK` &rarr; `NotificationResponse`

### 7.7 `PUT /api/notifications/user/{userId}/read-all`
- **Roles Allowed**: `ROLE_USER`, `ROLE_ADMIN`
- **Parameters**: `userId: Long` (path)
- **Response**: `200 OK` &rarr; `{ "message": "All notifications marked as read" }`

### 7.8 `GET /api/notifications`
- **Roles Allowed**: `ROLE_STAFF`, `ROLE_ADMIN`
- **Response**: `200 OK` &rarr; `List<NotificationResponse>`
- **Errors**: `403 Forbidden`

---

## 8. API Gateway Central Diagnostics (`API-GATEWAY` - Port 8083)
**Base Path**: `/api/gateway/**`

### 8.1 `GET /api/gateway/health`
- **Roles Allowed**: Public
- **Response**: `200 OK` &rarr; `{ "status": "UP", "service": "Aerobook API Gateway", "port": 8083, "routingEngine": "Spring Cloud Gateway WebFlux Reactive", "eurekaRegistered": true }`

### 8.2 `GET /api/gateway/services`
- **Roles Allowed**: Public
- **Response**: `200 OK` &rarr; Microservices architecture topology directory

### 8.3 `GET /api/gateway/endpoints`
- **Roles Allowed**: Public
- **Response**: `200 OK` &rarr; Master platform API catalog

### 8.4 `GET /api/gateway/rbac-matrix`
- **Roles Allowed**: Public
- **Response**: `200 OK` &rarr; Role-based access control matrix
