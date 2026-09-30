# API Synchronization Matrix & Mock Data Audit

This document joins every frontend API call with its corresponding backend endpoint, categorizing each contract into one of five statuses:
- **Match**: Frontend request and backend endpoint match perfectly in path, method, payload shape, response shape, and authorization.
- **Frontend fix**: Backend endpoint exists and is correct, but frontend makes incorrect request, uses outdated DTO, or needs response mapping.
- **Backend fix**: Backend endpoint exists but needs bug fixes, updated DTO fields, or permission adjustments.
- **Missing in backend**: Legitimate business feature required by the UI that currently lacks a backend service/endpoint (must be added in backend).
- **Unused**: Backend endpoint exists but is currently not invoked by the Angular application.

---

## 1. API Synchronization Matrix

| # | Domain / Feature | Frontend File & Call | Backend Service & Endpoint | Status | Resolution Details |
|---|------------------|----------------------|----------------------------|--------|---------------------|
| 1 | Auth - Login | `auth.service.ts`<br>`POST /api/auth/login` | `auth-service`<br>`POST /api/auth/login` | **Match** | Credentials validated, JWT and refresh tokens returned. |
| 2 | Auth - Customer Registration | `auth.service.ts`<br>`POST /api/auth/register` | `auth-service`<br>`POST /api/auth/register` | **Match** | User registered with `ROLE_USER`, profile synced with user-service. |
| 3 | Auth - Staff Registration | `auth.service.ts`<br>`POST /api/auth/register-staff` | `auth-service`<br>`POST /api/auth/register-staff` | **Match** | Secret `X-Staff-Setup-Key` verified, `ROLE_STAFF` issued. |
| 4 | Auth - Admin Registration | `auth.service.ts`<br>`POST /api/auth/register-admin` | `auth-service`<br>`POST /api/auth/register-admin` | **Match** | Secret `X-Admin-Setup-Key` verified, `ROLE_ADMIN` issued. |
| 5 | Auth - Token Refresh | `auth.interceptor.ts`<br>(Needed on 401) | `auth-service`<br>`POST /api/auth/refresh-token` | **Frontend fix** | Frontend interceptor currently logs out on 401 instead of attempting a single token refresh using stored `refreshToken`. Add automatic refresh flow. |
| 6 | Auth - Credentials Directory | `user.service.ts`<br>`GET /api/auth/credentials` | `auth-service`<br>`GET /api/auth/credentials` | **Match** | Requires `ROLE_ADMIN`. Returns list of all user credentials. |
| 7 | Auth - Role Promotion/Demotion | `user.service.ts`<br>`PUT /api/auth/credentials/{userId}/role?role={role}` | `auth-service`<br>`PUT /api/auth/credentials/{userId}/role` | **Match** | Requires `ROLE_ADMIN`. Updates security role in database. |
| 8 | Users - Get User by ID | `user.service.ts`<br>`GET /api/v1/users/{id}` | `user-service`<br>`GET /api/v1/users/{id}` | **Match** | Accessible by account owner or `ROLE_ADMIN`. |
| 9 | Users - Get User by Email | Not called in frontend | `user-service`<br>`GET /api/v1/users/email/{email}` | **Unused** | Can be leveraged for profile lookups if user ID is not yet in state. |
| 10 | Users - Update Profile | `user.service.ts`<br>`PUT /api/v1/users/{id}` | `user-service`<br>`PUT /api/v1/users/{id}` | **Match** | Updates personal details and demographics. |
| 11 | Users - List All Users | `user.service.ts`<br>`GET /api/v1/users` | `user-service`<br>`GET /api/v1/users` | **Match** | Requires `ROLE_ADMIN`. Returns complete customer directory. |
| 12 | Users - Create User Direct | Not called in frontend | `user-service`<br>`POST /api/v1/users` | **Unused** | Standard user registration goes through Auth Service. |
| 13 | Users - Delete User | `user.service.ts`<br>`DELETE /api/v1/users/{id}` | `user-service`<br>`DELETE /api/v1/users/{id}` | **Match** | Requires `ROLE_ADMIN`. Permanently removes user account. |
| 14 | Flights - Search Route | `flight.service.ts`<br>`GET /api/flights/search?source=&destination=` | `flight-service`<br>`GET /api/flights/search` | **Match** | Filters scheduled flights by origin and destination. |
| 15 | Flights - Browse Catalog | `flight.service.ts`<br>`GET /api/flights` | `flight-service`<br>`GET /api/flights` | **Match** | Returns all scheduled flights across the platform. |
| 16 | Flights - Get by ID | `flight.service.ts`<br>`GET /api/flights/{id}` | `flight-service`<br>`GET /api/flights/{id}` | **Match** | Fetches flight details, available seats, and base fare. |
| 17 | Flights - Update Status | `flight.service.ts`<br>`PUT /api/flights/{id}/status?status=` | `flight-service`<br>`PUT /api/flights/{id}/status` | **Match** | Requires `ROLE_STAFF` or `ROLE_ADMIN`. Updates operational state. |
| 18 | Flights - Schedule New Flight | `flight.service.ts`<br>`POST /api/admin/flights` | `flight-service`<br>`POST /api/admin/flights` | **Frontend fix** | Frontend sends `Partial<Flight>` where field names must strictly match `CreateFlightRequest` (`aircraftId`, `departureTime`, `arrivalTime` in ISO-8601). Ensure strict typing. |
| 19 | Flights - Update Schedule | `flight.service.ts`<br>`PUT /api/admin/flights/{id}` | `flight-service`<br>`PUT /api/admin/flights/{id}` | **Match** | Requires `ROLE_ADMIN`. Updates flight details. |
| 20 | Flights - Delete Flight | `flight.service.ts`<br>`DELETE /api/admin/flights/{id}` | `flight-service`<br>`DELETE /api/admin/flights/{id}` | **Match** | Requires `ROLE_ADMIN`. Deletes flight schedule. |
| 21 | Fleet - List Aircraft | `flight.service.ts`<br>`GET /api/admin/aircrafts` | `flight-service`<br>`GET /api/admin/aircrafts` | **Match** | Requires `ROLE_ADMIN`. Returns all fleet aircraft. |
| 22 | Fleet - Register Aircraft | `flight.service.ts`<br>`POST /api/admin/aircrafts` | `flight-service`<br>`POST /api/admin/aircrafts` | **Frontend fix** | Frontend models used `model`, `aircraftNumber`; backend requires `CreateAircraftRequest` (`aircraftCode`, `aircraftName`, `manufacturer`, `capacity`). Sync DTO property names. |
| 23 | Fleet - Delete Aircraft | `flight.service.ts`<br>`DELETE /api/admin/aircrafts/{id}` | `flight-service`<br>`DELETE /api/admin/aircrafts/{id}` | **Match** | Requires `ROLE_ADMIN`. Deletes aircraft entity. |
| 24 | Fares - List All Fares | `fare.service.ts`<br>`GET /api/fares` | `fare-service`<br>`GET /api/fares` | **Match** | Returns all configured flight fare structures. |
| 25 | Fares - Get by Flight ID | `fare.service.ts`<br>`GET /api/fares/flight/{flightId}` | `fare-service`<br>`GET /api/fares/flight/{flightId}` | **Match** | Fetches tiered prices for specific flight schedule. |
| 26 | Fares - Configure Fare | `fare.service.ts`<br>`POST /api/fares` | `fare-service`<br>`POST /api/fares` | **Frontend fix** | Frontend models had `taxRate`; backend DTO requires `taxPercentage` and `effectiveDate`. Sync typed DTO. |
| 27 | Fares - Update Fare | `fare.service.ts`<br>`PUT /api/fares/{id}` | `fare-service`<br>`PUT /api/fares/{id}` | **Frontend fix** | Same property alignment as #26. |
| 28 | Fares - Delete Fare | `fare.service.ts`<br>`DELETE /api/fares/{id}` | `fare-service`<br>`DELETE /api/fares/{id}` | **Match** | Requires `ROLE_ADMIN`. Deletes fare record. |
| 29 | Fares - Coupons & Promos | `admin-dashboard.component.ts`<br>`promos: PromoCode[]` (mock) | None | **Missing in backend** | Create coupon entity, repository, service, and REST endpoints in backend (`POST /api/fares/coupons`, `GET /api/fares/coupons`, `POST /api/fares/coupons/validate`). |
| 30 | Bookings - Create Booking | `booking.service.ts`<br>`POST /api/bookings` | `booking-service`<br>`POST /api/bookings` | **Match** | Validates flight & seats, assigns PNR, emits RabbitMQ event. |
| 31 | Bookings - Get by ID | `booking.service.ts`<br>`GET /api/bookings/{id}` | `booking-service`<br>`GET /api/bookings/{id}` | **Match** | Returns booking details and passenger records. |
| 32 | Bookings - Get by PNR | `booking.service.ts`<br>`GET /api/bookings/pnr/{pnr}` | `booking-service`<br>`GET /api/bookings/pnr/{pnr}` | **Match** | Used by Passenger and Staff search. |
| 33 | Bookings - Get by User ID | `booking.service.ts`<br>`GET /api/bookings/user/{userId}` | `booking-service`<br>`GET /api/bookings/user/{userId}` | **Match** | Passenger "My Trips" history list. |
| 34 | Bookings - Passenger Manifest | `booking.service.ts`<br>`GET /api/bookings/flight/{flightId}` | `booking-service`<br>`GET /api/bookings/flight/{flightId}` | **Match** | Staff operations manifest for boarding gates. |
| 35 | Bookings - Master List All | `booking.service.ts`<br>`GET /api/bookings` | `booking-service`<br>`GET /api/bookings` | **Match** | Staff & Admin master reservation directory. |
| 36 | Bookings - Cancel Booking | `booking.service.ts`<br>`DELETE /api/bookings/{id}` | `booking-service`<br>`DELETE /api/bookings/{id}` | **Match** | Safe cancellation with status update to `CANCELLED`. |
| 37 | Check-In - Perform Check-In | `checkin.service.ts`<br>`POST /api/check-ins` | `check-in-service`<br>`POST /api/check-ins` | **Match** | Allocates seat, generates boarding pass (`BP-1-12A`). |
| 38 | Check-In - Get by Booking ID | `checkin.service.ts`<br>`GET /api/check-ins/booking/{bookingId}` | `check-in-service`<br>`GET /api/check-ins/booking/{bookingId}` | **Match** | Web check-in confirmation lookup. |
| 39 | Check-In - List All | `checkin.service.ts`<br>`GET /api/check-ins` | `check-in-service`<br>`GET /api/check-ins` | **Match** | Master check-in audit for Staff & Admin. |
| 40 | Check-In - Gate Boarding Pass Scanner | `checkin.service.ts`<br>`POST /api/check-ins/verify?boardingPassNumber=` | `check-in-service`<br>`POST /api/check-ins/verify` | **Match** | Updates status to `BOARDED`. |
| 41 | Notifications - User Alerts | `notification.service.ts`<br>`GET /api/notifications/user/{userId}` | `notification-service`<br>`GET /api/notifications/user/{userId}` | **Match** | In-app passenger notification bell drawer. |
| 42 | Notifications - Unread Count | `notification.service.ts`<br>`GET /api/notifications/unread-count/{userId}` | `notification-service`<br>`GET /api/notifications/unread-count/{userId}` | **Match** | Navbar badge count indicator. |
| 43 | Notifications - Mark Read | `notification.service.ts`<br>`PUT /api/notifications/{id}/read` | `notification-service`<br>`PUT /api/notifications/{id}/read` | **Match** | Stamps read timestamp. |
| 44 | Notifications - Mark All Read | `notification.service.ts`<br>`PUT /api/notifications/user/{userId}/read-all` | `notification-service`<br>`PUT /api/notifications/user/{userId}/read-all` | **Match** | Batch mark read. |
| 45 | Notifications - Broadcast | `notification.service.ts`<br>`POST /api/notifications/broadcast` | `notification-service`<br>`POST /api/notifications/broadcast` | **Match** | Staff & Admin terminal flight announcements. |
| 46 | Notifications - All Alerts | `notification.service.ts`<br>`GET /api/notifications` | `notification-service`<br>`GET /api/notifications` | **Match** | Platform notification audit log. |
| 47 | Notifications - HTML Emails | None | None | **Missing in backend** | Notification service needs `spring-boot-starter-mail` with Thymeleaf templates for responsive HTML booking confirmations and operational notices. |
| 48 | Payments - Razorpay Service | None | None | **Missing in backend** | Dedicated `payment-service` (Port 8092) required for creating Razorpay orders, HMAC-SHA256 signature verification, webhook processing, and refunds. |
| 49 | Admin - Dashboard Summary | Static counters in `admin-dashboard.component.ts` | None | **Missing in backend** | Need `GET /api/admin/dashboard/summary` providing real computed totals: flights, operational vs maintenance aircraft, passenger accounts, gross booking value, open incidents, and real system health. |
| 50 | Reports - Incident Reports | `incidentReports: UserIncidentReport[]` (mock) | None | **Missing in backend** | Endpoints needed for logging disruptive passenger / flight incidents (`POST /api/incidents`, `GET /api/incidents`, `PUT /api/incidents/{id}/status`). |

---

## 2. Comprehensive Mock Data Audit

Every location in the Angular frontend that contains mock, fake, placeholder, hard-coded fallback, or simulated data has been identified:

### 2.1 `src/app/features/admin/admin-dashboard.component.ts`
1. **Mock Seat Map** (`mockSeatMap: SeatItem[]`, lines 2248-2274): 24 hardcoded seat objects (`1A` to `4F`) with static `isBooked` and `isBusiness` flags used for visual preview.
2. **Hardcoded Microservices Latencies** (`microservices = [...]`, lines 2276-2283): Static array containing fake latency figures (12ms, 18ms, 15ms, etc.) and hardcoded "UP" statuses instead of real actuator health checks.
3. **Hardcoded Audit Logs** (`auditLogs: AdminAuditEntry[]`, lines 2181-2200): Static array of 4 fake security audit records.
4. **Hardcoded Promo Codes** (`loadPromos()`, lines 2551-2558): Static array of 3 promo codes (`AERO2026`, `FLYHIGH20`, `STAFF10`).
5. **Hardcoded Incident Reports** (`loadIncidentReports()`, lines 2559-2565): Static array of 2 mock passenger incidents (`Karan Malhotra`, `Amit Verma`).
6. **Hardcoded Admin Profile** (`adminProfile: AdminProfile`, lines 2169-2179): Static object for "Amir Sohel" with hardcoded joined date and security clearance.
7. **Error Fallbacks Pretending to be Data**:
   - `loadFleet()` error block populates static mock aircraft.
   - `loadUsers()` error block populates static mock users.
   - `loadFares()` error block populates static mock fares.
   - `loadBookings()` error block populates static mock bookings.
   - `createAircraft()`, `createFlight()`, `createPromo()`, `reportIncident()`, `togglePromoStatus()` locally mutate in-memory arrays when requests fail.
8. **Static Stat Card Numbers**: Hardcoded "99.98% System Uptime" and static badge percentages.

### 2.2 `src/app/features/staff/staff-dashboard.component.ts`
1. **Mock Seat Map** (`mockSeatMap: SeatItem[]`, lines 1049-1062): 12 hardcoded seat records.
2. **Error Fallback Flights** (`loadFlights()`, lines 1135-1145): Fake `AI-101` flight inserted on error.
3. **Error Fallback Check-Ins** (`loadCheckIns()`, lines 1149-1158): Fake `Priya Sharma` check-in inserted on error.
4. **Error Fallback Manifest** (`loadManifest()`, lines 1162-1175): Fake passengers manifest inserted on error.
5. **Simulated Gate Scanner** (`performScan()`, lines 1276-1285): Fake demo scan result returned on error.
6. **Simulated Counter Check-In** (`performCounterCheckIn()`, lines 1304-1311): Fake boarding pass `BP-...` generated on error.
7. **Simulated Announcement Broadcast** (`broadcastDelayAlert()`, lines 1322-1325): Toast displayed on error pretending broadcast succeeded.
8. **Hardcoded Staff Profile** (`staffProfile`, lines 1032-1045): Static staff coordinates for Delhi Terminal 3.

### 2.3 `src/app/features/customer/customer-dashboard.component.ts`
1. **Seed Fallback Bookings** (`seedFallbackBookings()`, lines 2078-2121): 3 hardcoded bookings (`PNR-AB1001`, `PNR-AB1002`, `PNR-AB1003`) loaded when API returns empty or error.
2. **Seed Fallback Flights** (`seedFallbackFlights()`, lines 2144-2200): 3 hardcoded flights loaded when API returns empty or error.
3. **Seed Fallback Notifications** (`seedFallbackNotifications()`, lines 2213-2244): 3 hardcoded notification items loaded when API returns empty or error.
4. **Simulated Booking Submission** (`submitBooking()`, lines 2373-2390): Generates a random `PNR-AB...` locally when backend call fails.
5. **Simulated Check-In** (`performCheckIn()`, lines 2436-2450): Generates a local `BP-...` boarding pass when backend call fails.
6. **Client-Side Promo Code Discount** (`applyPromoCode()`, lines 2318-2335): Hardcoded codes (`GOLD15`, `SPECIAL15`) validated purely on client side.
7. **Client-Side Fare Calculation** (`calculateGrandTotal()`, lines 2297-2316): Hardcoded convenience fees (₹450) and class surcharges (₹3500) computed in frontend instead of by backend fare engine.
8. **Simulated Mutations on Error**:
   - `markNotificationAsRead()` & `markAllNotificationsAsRead()` error fallbacks mutate local arrays.
   - `updateProfile()` error fallback updates local traveler profile.
   - `confirmCancelBooking()` error fallback sets status to `CANCELLED` locally.

### 2.4 `src/app/core/services/gamification.service.ts`
1. **LocalStorage-only Persistence**:
   - The entire rewards and loyalty tier system runs in browser `localStorage` under `aerobook_gamification_profile`.
   - Seed profile for Priya Sharma (24,850 XP, Level 3 Gold Aviator, 8 badges, 4 quests, 3 discount vouchers) is hardcoded.
