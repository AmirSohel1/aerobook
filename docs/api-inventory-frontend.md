# Frontend API Call Inventory

This document catalogs every HTTP call made by the AeroBook Angular frontend, including source file location, HTTP method, Gateway endpoint URL, request payload shape, expected response shape, and target user role.

All requests target the central API Gateway base URL (`http://localhost:8083`), with the JWT Bearer token attached via `AuthInterceptor` (except public endpoints).

---

## 1. Authentication Service (`AuthService`)
**Source File**: `src/app/core/services/auth.service.ts`

| # | HTTP Method | Gateway Endpoint URL | Request Shape | Expected Response Shape | Role / Audience | Purpose |
|---|-------------|----------------------|---------------|-------------------------|-----------------|---------|
| 1 | `POST` | `/api/auth/login` | `LoginRequest`<br>`{ email: string, password: string }` | `AuthResponse`<br>`{ accessToken: string, refreshToken: string, tokenType: string, role: RoleType, userId: number }` | Public / All | Authenticate user credentials and establish session |
| 2 | `POST` | `/api/auth/register` | `RegisterRequest`<br>`{ firstName: string, lastName: string, email: string, phoneNumber?: string, dateOfBirth?: string, nationality?: string, password: string }` | `AuthResponse` | Public (Customer) | Register new passenger account (`ROLE_USER`) |
| 3 | `POST` | `/api/auth/register-staff` | Headers: `X-Staff-Setup-Key`<br>Body: `RegisterRequest` | `AuthResponse` | Public / Admin Bootstrap | Register new airport operations staff (`ROLE_STAFF`) |
| 4 | `POST` | `/api/auth/register-admin` | Headers: `X-Admin-Setup-Key`<br>Body: `RegisterRequest` | `AuthResponse` | Public / Admin Bootstrap | Register or elevate master administrator (`ROLE_ADMIN`) |

---

## 2. Flight Service (`FlightService`)
**Source File**: `src/app/core/services/flight.service.ts`

| # | HTTP Method | Gateway Endpoint URL | Request Shape | Expected Response Shape | Role / Audience | Purpose |
|---|-------------|----------------------|---------------|-------------------------|-----------------|---------|
| 5 | `GET` | `/api/flights/search?source={source}&destination={destination}` | Query params: `source: string`, `destination: string` | `Flight[]`<br>`[{ id: number, flightNumber: string, airlineName: string, source: string, destination: string, departureTime: string, arrivalTime: string, totalSeats: number, availableSeats: number, baseFare: number, status: string }]` | Public / Customer / All | Search scheduled flights by origin and destination |
| 6 | `GET` | `/api/flights` | None | `Flight[]` | Public / Customer / Staff / Admin | Browse all active scheduled flights catalog |
| 7 | `GET` | `/api/flights/{id}` | Path param: `id: number` | `Flight` | Public / Customer / Staff / Admin | Get detailed flight schedule & seat availability |
| 8 | `PUT` | `/api/flights/{id}/status?status={status}` | Path param: `id: number`<br>Query param: `status: string` | `Flight` | `ROLE_STAFF`, `ROLE_ADMIN` | Update operational flight status (`BOARDING`, `DELAYED`, `DEPARTED`, `COMPLETED`) |
| 9 | `POST` | `/api/admin/flights` | `Partial<Flight>`<br>`{ flightNumber, airlineName, source, destination, departureTime, arrivalTime, totalSeats, baseFare, aircraftId }` | `Flight` | `ROLE_ADMIN` | Schedule a new commercial flight |
| 10 | `PUT` | `/api/admin/flights/{id}` | Path param: `id: number`<br>Body: `Partial<Flight>` | `Flight` | `ROLE_ADMIN` | Update scheduled flight route, timings, or pricing |
| 11 | `DELETE` | `/api/admin/flights/{id}` | Path param: `id: number` | Text message string | `ROLE_ADMIN` | Delete/cancel a flight schedule |
| 12 | `GET` | `/api/admin/aircrafts` | None | `Aircraft[]`<br>`[{ id: number, model: string, aircraftNumber: string, capacity: number, status: string, manufacturer?: string }]` | `ROLE_ADMIN` | List all fleet aircraft |
| 13 | `POST` | `/api/admin/aircrafts` | `Partial<Aircraft>`<br>`{ aircraftCode, aircraftName, manufacturer, capacity }` | `Aircraft` | `ROLE_ADMIN` | Register a new aircraft model into fleet |
| 14 | `DELETE` | `/api/admin/aircrafts/{id}` | Path param: `id: number` | Text message string | `ROLE_ADMIN` | Remove an aircraft from fleet inventory |

---

## 3. Fare Service (`FareService`)
**Source File**: `src/app/core/services/fare.service.ts`

| # | HTTP Method | Gateway Endpoint URL | Request Shape | Expected Response Shape | Role / Audience | Purpose |
|---|-------------|----------------------|---------------|-------------------------|-----------------|---------|
| 15 | `GET` | `/api/fares` | None | `Fare[]`<br>`[{ id: number, flightId: number, economyFare: number, businessFare: number, firstClassFare: number, taxRate: number, discountPercentage: number }]` | `ROLE_USER`, `ROLE_ADMIN` | List all configured flight fare structures |
| 16 | `GET` | `/api/fares/flight/{flightId}` | Path param: `flightId: number` | `Fare` | `ROLE_USER`, `ROLE_ADMIN` | Get active fare pricing breakdown for a flight |
| 17 | `POST` | `/api/fares` | `Partial<Fare>` | `Fare` | `ROLE_ADMIN` | Create new tiered cabin pricing structure |
| 18 | `PUT` | `/api/fares/{id}` | Path param: `id: number`<br>Body: `Partial<Fare>` | `Fare` | `ROLE_ADMIN` | Update pricing tiers, GST tax, or discounts |
| 19 | `DELETE` | `/api/fares/{id}` | Path param: `id: number` | Text message string | `ROLE_ADMIN` | Delete fare configuration |

---

## 4. Booking Service (`BookingService`)
**Source File**: `src/app/core/services/booking.service.ts`

| # | HTTP Method | Gateway Endpoint URL | Request Shape | Expected Response Shape | Role / Audience | Purpose |
|---|-------------|----------------------|---------------|-------------------------|-----------------|---------|
| 20 | `POST` | `/api/bookings` | `BookingRequest`<br>`{ userId: number, flightId: number, passengers: [{ firstName: string, lastName: string, age: number, gender: string }] }` | `Booking`<br>`{ bookingId: number, pnr: string, userId: number, flightId: number, bookingDate: string, totalFare: number, status: string, passengers: Passenger[] }` | `ROLE_USER`, `ROLE_ADMIN` | Create and confirm flight reservation with PNR |
| 21 | `GET` | `/api/bookings/{id}` | Path param: `id: number` | `Booking` | `ROLE_USER`, `ROLE_ADMIN` | Lookup reservation by database ID |
| 22 | `GET` | `/api/bookings/pnr/{pnr}` | Path param: `pnr: string` | `Booking` | `ROLE_USER`, `ROLE_STAFF`, `ROLE_ADMIN` | Lookup reservation by PNR |
| 23 | `GET` | `/api/bookings/user/{userId}` | Path param: `userId: number` | `Booking[]` | `ROLE_USER`, `ROLE_ADMIN` | List all bookings for a passenger account |
| 24 | `GET` | `/api/bookings/flight/{flightId}` | Path param: `flightId: number` | `Booking[]` | `ROLE_STAFF`, `ROLE_ADMIN` | Retrieve flight passenger manifest |
| 25 | `GET` | `/api/bookings` | None | `Booking[]` | `ROLE_STAFF`, `ROLE_ADMIN` | Master list of all airline reservations |
| 26 | `DELETE` | `/api/bookings/{bookingId}` | Path param: `bookingId: number` | Text message string | `ROLE_USER`, `ROLE_ADMIN` | Cancel confirmed reservation |

---

## 5. Check-In Service (`CheckInService`)
**Source File**: `src/app/core/services/checkin.service.ts`

| # | HTTP Method | Gateway Endpoint URL | Request Shape | Expected Response Shape | Role / Audience | Purpose |
|---|-------------|----------------------|---------------|-------------------------|-----------------|---------|
| 27 | `POST` | `/api/check-ins` | `CheckInRequest`<br>`{ bookingId: number, passengerName: string, seatNumber?: string }` | `CheckIn`<br>`{ id: number, bookingId: number, passengerName: string, seatNumber: string, boardingPassNumber: string, status: string, checkedInAt: string }` | `ROLE_USER`, `ROLE_STAFF`, `ROLE_ADMIN` | Perform passenger check-in and issue boarding pass |
| 28 | `GET` | `/api/check-ins/booking/{bookingId}` | Path param: `bookingId: number` | `CheckIn` | `ROLE_USER`, `ROLE_STAFF`, `ROLE_ADMIN` | Lookup check-in confirmation by booking ID |
| 29 | `GET` | `/api/check-ins` | None | `CheckIn[]` | `ROLE_STAFF`, `ROLE_ADMIN` | Master audit list of all check-in confirmations |
| 30 | `POST` | `/api/check-ins/verify?boardingPassNumber={boardingPassNumber}` | Query param: `boardingPassNumber: string` | `CheckIn` | `ROLE_STAFF`, `ROLE_ADMIN` | Departure gate boarding pass scanner |

---

## 6. Notification Service (`NotificationService`)
**Source File**: `src/app/core/services/notification.service.ts`

| # | HTTP Method | Gateway Endpoint URL | Request Shape | Expected Response Shape | Role / Audience | Purpose |
|---|-------------|----------------------|---------------|-------------------------|-----------------|---------|
| 31 | `GET` | `/api/notifications/user/{userId}` | Path param: `userId: number` | `Notification[]`<br>`[{ id: number, userId?: number, title: string, message: string, type: string, status: string, flightNumber?: string, createdAt: string, readAt?: string }]` | `ROLE_USER`, `ROLE_ADMIN` | Get passenger personal notifications |
| 32 | `GET` | `/api/notifications/unread-count/{userId}` | Path param: `userId: number` | `{ userId: number, unreadCount: number }` | `ROLE_USER`, `ROLE_ADMIN` | Get unread notification badge counter |
| 33 | `PUT` | `/api/notifications/{id}/read` | Path param: `id: number` | `Notification` | `ROLE_USER`, `ROLE_ADMIN` | Mark single notification as READ |
| 34 | `PUT` | `/api/notifications/user/{userId}/read-all` | Path param: `userId: number` | `{ message: string }` | `ROLE_USER`, `ROLE_ADMIN` | Mark all user notifications as READ |
| 35 | `POST` | `/api/notifications/broadcast` | `BroadcastRequest`<br>`{ title: string, message: string, type: string, flightNumber?: string }` | `Notification` | `ROLE_STAFF`, `ROLE_ADMIN` | Broadcast operational delay or gate alert |
| 36 | `GET` | `/api/notifications` | None | `Notification[]` | `ROLE_STAFF`, `ROLE_ADMIN` | Master platform notification audit log |

---

## 7. User Profile Service (`UserService`)
**Source File**: `src/app/core/services/user.service.ts`

| # | HTTP Method | Gateway Endpoint URL | Request Shape | Expected Response Shape | Role / Audience | Purpose |
|---|-------------|----------------------|---------------|-------------------------|-----------------|---------|
| 37 | `GET` | `/api/v1/users/{id}` | Path param: `id: number` | `User`<br>`{ userId: number, firstName: string, lastName: string, email: string, phoneNumber?: string, dateOfBirth?: string, nationality?: string, role: string }` | `ROLE_USER`, `ROLE_ADMIN` | Get user profile details by ID |
| 38 | `PUT` | `/api/v1/users/{id}` | Path param: `id: number`<br>Body: `Partial<User>` | `User` | `ROLE_USER`, `ROLE_ADMIN` | Update passenger / staff profile attributes |
| 39 | `GET` | `/api/v1/users` | None | `User[]` | `ROLE_ADMIN` | Master list of all registered platform users |
| 40 | `GET` | `/api/auth/credentials` | None | `CredentialResponse[]`<br>`[{ id: number, email: string, role: string, createdAt: string, lastLogin: string }]` | `ROLE_ADMIN` | Security credential directory & role audit |
| 41 | `PUT` | `/api/auth/credentials/{userId}/role?role={role}` | Path param: `userId: number`<br>Query param: `role: string` | `CredentialResponse` | `ROLE_ADMIN` | Update user role (`ROLE_USER`, `ROLE_STAFF`, `ROLE_ADMIN`) |
| 42 | `DELETE` | `/api/v1/users/{userId}` | Path param: `userId: number` | Text message string | `ROLE_ADMIN` | Delete user profile and account |
