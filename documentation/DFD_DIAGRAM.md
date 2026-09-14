# AeroBook Enterprise Platform — Data Flow Diagram (DFD) Specification

> **Theme**: Dark Canvas Background (`#000000`), Crisp White Marker Strokes (`#FFFFFF`), Enterprise Data Flow & Storage Architecture.

---

## 1. DFD Level 0: System Context Diagram

The Level 0 Context Diagram establishes the boundary of the **AeroBook Reservation Platform**, identifying external source and sink entities and the macro-level data exchanges entering and exiting the platform boundary.

```mermaid
%%{init: {
  'theme': 'base',
  'themeVariables': {
    'darkMode': true,
    'background': '#000000',
    'primaryColor': '#000000',
    'primaryTextColor': '#ffffff',
    'primaryBorderColor': '#ffffff',
    'lineColor': '#ffffff',
    'secondaryColor': '#000000',
    'tertiaryColor': '#000000'
  }
}}%%
graph LR
    classDef entityBox fill:#09090b,stroke:#ffffff,stroke-width:2px,color:#ffffff;
    classDef systemBox fill:#18181b,stroke:#38bdf8,stroke-width:3px,color:#ffffff;

    E1["External Entity: E1<br/><b>PASSENGER</b><br/>(Web & Mobile App)"]:::entityBox
    E2["External Entity: E2<br/><b>AIRLINE ADMIN</b><br/>(Fleet & Operations)"]:::entityBox
    E3["External Entity: E3<br/><b>AIRPORT GATE STAFF</b><br/>(Barcode Scanner)"]:::entityBox
    E4["External Entity: E4<br/><b>PAYMENT GATEWAY</b><br/>(Stripe / Razorpay)"]:::entityBox
    E5["External Entity: E5<br/><b>NOTIFICATION SERVICE</b><br/>(SendGrid / Twilio)"]:::entityBox

    SYS(("<b>0.0<br/>AeroBook Platform</b><br/>Distributed Cloud Core")):::systemBox

    E1 -->|"1. Credentials, Search, Booking, Check-In"| SYS
    SYS -->|"2. JWT, Flight Catalog, PNR Ticket, QR Pass"| E1

    E2 -->|"3. Fleet Models, Flight Schedules, Fare Rules"| SYS
    SYS -->|"4. Manifest Logs & Operational Telemetry"| E2

    SYS -->|"5. Payment Charge Authorization Request"| E4
    E4 -->|"6. Transaction Settlement Status & Txn ID"| SYS

    E3 -->|"7. Gate Boarding Scan (QR Payload)"| SYS
    SYS -->|"8. Embarkation Clearance Approval"| E3

    SYS -->|"9. Email & SMS Dispatch Job Payload"| E5
    E5 -.->|"10. Out-of-band Delivery"| E1
```

---

## 2. DFD Level 1: Subsystem Decomposition Diagram

The Level 1 DFD decomposes the system into **7 Core Processes (P1.0 to P7.0)**, **7 Isolated Repositories (D1 to D7)**, and shows the explicit data pipelines connecting them.

```mermaid
%%{init: {
  'theme': 'base',
  'themeVariables': {
    'darkMode': true,
    'background': '#000000',
    'primaryColor': '#000000',
    'primaryTextColor': '#ffffff',
    'primaryBorderColor': '#ffffff',
    'lineColor': '#ffffff',
    'secondaryColor': '#000000',
    'tertiaryColor': '#000000'
  }
}}%%
graph TD
    classDef entity fill:#09090b,stroke:#ffffff,stroke-width:2px,color:#ffffff;
    classDef process fill:#18181b,stroke:#38bdf8,stroke-width:2px,color:#ffffff;
    classDef coreProcess fill:#27272a,stroke:#ffffff,stroke-width:3px,color:#ffffff;
    classDef dbStore fill:#020617,stroke:#4ade80,stroke-width:2px,color:#ffffff;
    classDef queueStore fill:#1e1b4b,stroke:#c084fc,stroke-width:2px,color:#ffffff;

    E1["Entity E1: PASSENGER"]:::entity
    E2["Entity E2: AIRLINE ADMIN"]:::entity
    E3["Entity E3: GATE STAFF"]:::entity
    E4["Entity E4: PAYMENT GATEWAY"]:::entity
    E5["Entity E5: NOTIFICATION SVC"]:::entity

    P1["P 1.0<br/>Authentication Engine<br/>(auth-service:8082)"]:::process
    P2["P 2.0<br/>Passenger Profile Svc<br/>(user-service:8084)"]:::process
    P3["P 3.0<br/>Flight Search & Fleet<br/>(flight-service:8087)"]:::process
    P4["P 4.0<br/>Dynamic Pricing Engine<br/>(fare-service:8089)"]:::process
    P5["P 5.0 (CORE)<br/>Booking Orchestration<br/>(booking-service:8088)"]:::coreProcess
    P6["P 6.0<br/>Web Check-In & Gate<br/>(check-in-service:8090)"]:::process
    P7["P 7.0<br/>Async Event Consumer<br/>(RabbitMQ Worker)"]:::process

    D1[("D1: aerobook_auth_db<br/>users, roles, tokens")]:::dbStore
    D2[("D2: aerobook_user_db<br/>profiles, passports")]:::dbStore
    D3[("D3: aerobook_flight_db<br/>flights, aircraft, seats")]:::dbStore
    D4[("D4: aerobook_fare_db<br/>fares, surge_rules")]:::dbStore
    D5[("D5: aerobook_booking_db<br/>bookings, passengers, pnr")]:::dbStore
    D6[("D6: aerobook_checkin_db<br/>check_ins, boarding_passes")]:::dbStore
    D7[("D7: RabbitMQ Broker<br/>queue: booking.events")]:::queueStore

    %% Auth & User Flows
    E1 -->|"1. User Credentials"| P1
    P1 -->|"2. Bearer JWT Token"| E1
    P1 <-->|"Verify & Hash"| D1

    E1 <-->|"Profile & Passport"| P2
    P2 <-->|"Persist Profile"| D2

    %% Flight Discovery & Admin Schedules
    E1 -->|"3. Search Origin, Dest, Date"| P3
    P3 -->|"4. Available Flight Catalog"| E1
    E2 -->|"Fleet Models & Timetables"| P3
    P3 <-->|"Inventory & Seat Locks"| D3

    %% Pricing Engine
    E2 -->|"Base Fares & Multipliers"| P4
    P4 <-->|"Read Pricing Rules"| D4
    P3 -.->|"Cabin Occupancy Ratio"| P4

    %% Core Booking Pipeline
    E1 -->|"5. Create Booking Request"| P5
    P3 -->|"Seat Lock Confirmation"| P5
    P4 -->|"Dynamic Fare Calculation"| P5
    P5 -->|"6. Authorize Charge ($)"| E4
    E4 -->|"7. Payment Confirmed (TxnId)"| P5
    P5 -->|"Save Confirmed PNR"| D5
    P5 -->|"8. Booking Confirmation + PNR"| E1
    P5 -->|"Publish BookingConfirmedEvent"| D7

    %% Web Check-in & Gate Verification
    E1 -->|"9. Web Check-In Request"| P6
    P6 -.->|"Verify PNR State"| D5
    P6 -->|"Store Boarding Pass"| D6
    P6 -->|"10. Digital QR Boarding Pass"| E1

    E3 -->|"11. Scan QR Barcode"| P6
    P6 <-->|"Validate & Update EMBARKED"| D6
    P6 -->|"12. Gate Clearance Approval"| E3

    %% Notification Pipeline
    D7 -->|"Consume Event Payload"| P7
    P7 -->|"Dispatch Email & SMS"| E5
    E5 -.->|"Async Flight Alert"| E1
```

---

## 3. Comprehensive Data Dictionary

### 3.1 External Entities (Sources & Sinks)

| Entity ID | Name                     | Role              | Primary Data Generated (Source)                                                                               | Primary Data Received (Sink)                                                                             |
| :-------- | :----------------------- | :---------------- | :------------------------------------------------------------------------------------------------------------ | :------------------------------------------------------------------------------------------------------- |
| **E1**    | **Passenger**            | Traveler          | Credentials, Profile updates, Flight queries, Passenger manifests, Payment authorizations, Check-in requests. | JWT Bearer tokens, Flight catalogs, Booking receipts (PNR), Electronic boarding passes (QR), SMS alerts. |
| **E2**    | **Airline Admin**        | Operations Staff  | Aircraft configurations, Seat matrices, Route timetables, Base pricing rules, Surge thresholds.               | Operational logs, Passenger manifests, Flight revenue telemetry.                                         |
| **E3**    | **Airport Gate Staff**   | Ground Operations | 2D QR Barcode scan event at airport boarding gate.                                                            | Visual clearance signal (Green/Red), Audio confirmation, Embarkation status.                             |
| **E4**    | **Payment Gateway**      | Financial Service | Payment settlement status (`SUCCESS`/`FAILED`), Transaction IDs (`txn_id`), Risk fraud score.                 | Charge authorization payload (`amount`, `currency`, `payment_method_token`, `pnr`).                      |
| **E5**    | **Notification Service** | Delivery Channel  | Message dispatch status callbacks (Delivered, Bounced, Queued).                                               | Templated HTML ticket summaries, SMS PNR booking confirmation strings, gate change notices.              |

---

### 3.2 Processes (Transformation Logic)

| Process ID | Name                                | Microservice              | Input Data Flows                                                             | Output Data Flows                                                            | Underlying Transformation Logic                                                                                                                                                      |
| :--------- | :---------------------------------- | :------------------------ | :--------------------------------------------------------------------------- | :--------------------------------------------------------------------------- | :----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **P 1.0**  | **Authentication & Access Control** | `auth-service` (8082)     | Credentials (`username`, `password`), Registration payloads, Refresh tokens. | Signed JWT tokens, Expiration timestamps, Account validation claims.         | Evaluates passwords using Spring Security BCryptPasswordEncoder (strength 10). Issues signed HMAC-SHA256 tokens carrying claims and authority sets (`ROLE_PASSENGER`, `ROLE_ADMIN`). |
| **P 2.0**  | **Passenger Profile Store**         | `user-service` (8084)     | User profile updates, Passport identifiers, Travel preferences.              | Sanitized customer entity, Emergency contact confirmation.                   | Validates international passport formatting and expiration dates. Enforces user data sovereignty and GDPR profile rectification.                                                     |
| **P 3.0**  | **Flight Search & Scheduling**      | `flight-service` (8087)   | Search filters (`origin`, `dest`, `date`, `cabinClass`), Fleet schedules.    | Available flight list, Cabin seat availability maps, Temporary seat locks.   | Filters flights matching origin/destination IATA codes and dates. Implements atomic seat locks with 10-minute hold expirations to prevent double-booking.                            |
| **P 4.0**  | **Dynamic Fare Engine**             | `fare-service` (8089)     | Flight ID, Cabin class, Booking timestamp, Demand occupancy ratio.           | Itemized fare quote (Base fare, Demand surge, Fuel surcharge, Taxes, Total). | Computes real-time dynamic pricing using algorithm: `FinalPrice = BaseFare * CabinMultiplier * (1 + SurgeRate) + AirportTaxes`.                                                      |
| **P 5.0**  | **Flight Booking & PNR Workflow**   | `booking-service` (8088)  | Passenger manifest, Flight ID, Selected seats, Payment token.                | Unique 6-character PNR, Electronic ticket record, `BookingConfirmedEvent`.   | Core transaction coordinator. Verifies seat holds with P3.0, executes payment charge with E4, persists reservation in D5, and publishes AMQP event to D7.                            |
| **P 6.0**  | **Web Check-In & Gate Clearance**   | `check-in-service` (8090) | Check-in requests (`pnr`, `lastName`), Gate scanner 2D barcode payload.      | Digital Boarding Pass, Encrypted QR code, Gate clearance validation.         | Enforces flight departure window rules (T-24h to T-1h). Validates PNR status against D5. Generates encrypted 2D QR codes containing PNR, seat, and SHA-256 digital signature.        |
| **P 7.0**  | **Async Notification Dispatcher**   | RabbitMQ Consumer         | `BookingConfirmedEvent` & `CheckInCompletedEvent` payloads from AMQP queue.  | Dispatch requests to external SMTP SendGrid API & SMS Twilio API.            | Consumes messages from RabbitMQ `booking.events` queue. Hydrates HTML email itineraries and triggers asynchronous notifications with retry backoff.                                  |

---

### 3.3 Data Stores (Isolated Databases & Queues)

| Store ID | Name                  | Database Tech | Encapsulated Tables / Queues                         | Coupling & Access Constraints                                           |
| :------- | :-------------------- | :------------ | :--------------------------------------------------- | :---------------------------------------------------------------------- |
| **D1**   | `aerobook_auth_db`    | MySQL 8.0     | `users`, `roles`, `user_roles`, `refresh_tokens`     | Strictly private to `auth-service`. Zero direct external queries.       |
| **D2**   | `aerobook_user_db`    | MySQL 8.0     | `user_profiles`, `passports`, `addresses`            | Strictly private to `user-service`. Accessed via authenticated REST.    |
| **D3**   | `aerobook_flight_db`  | MySQL 8.0     | `flights`, `aircraft`, `seats`, `seat_inventories`   | Strictly private to `flight-service`. High-read caching optimized.      |
| **D4**   | `aerobook_fare_db`    | MySQL 8.0     | `fares`, `surge_multipliers`, `class_pricing_rules`  | Strictly private to `fare-service`. Deterministic pricing calculations. |
| **D5**   | `aerobook_booking_db` | MySQL 8.0     | `bookings`, `passengers`, `booking_payments`, `logs` | Strictly private to `booking-service`. Transactional ACID core.         |
| **D6**   | `aerobook_checkin_db` | MySQL 8.0     | `check_ins`, `boarding_passes`, `gate_verifications` | Strictly private to `check-in-service`. Fast gate validation lookups.   |
| **D7**   | `RabbitMQ Broker`     | AMQP 0-9-1    | Exchange: `aerobook.direct`, Queue: `booking.events` | Decoupled durable message queue with Dead-Letter Exchange (DLX).        |

---

## 4. End-to-End Data Flow Execution Cycles

### Cycle 1: Flight Search to Confirmed Booking (Data Flow 1 → 8)

1. **Flow 1 (Credentials)**: Passenger (E1) sends `POST /api/auth/login` to `P1.0`. Credentials validated against `D1`.
2. **Flow 2 (JWT)**: `P1.0` returns signed Bearer JWT token to E1.
3. **Flow 3 (Search Query)**: E1 submits flight search filters (`origin=JFK`, `destination=LHR`, `date=2026-10-15`) to `P3.0`.
4. **Flow 4 (Catalog Quote)**: `P3.0` queries `D3` and coordinates with `P4.0` (`D4`) to return matching flight cards with dynamic prices.
5. **Flow 5 (Booking Creation)**: E1 selects seats and submits booking request containing passenger manifest and payment token to `P5.0`.
6. **Flow 6 & 7 (Payment Settlement)**: `P5.0` holds seats with `P3.0`, then calls `E4` (Payment Gateway). `E4` settles the transaction and returns `txn_id`.
7. **Flow 8 (PNR Commitment)**: `P5.0` commits the booking with status `CONFIRMED` and unique 6-character PNR in `D5`, returns confirmation to E1, and publishes `BookingConfirmedEvent` to `D7`.

### Cycle 2: Web Check-In & Airport Gate Embarkation (Data Flow 9 → 12)

1. **Flow 9 (Check-In Request)**: Passenger (E1) accesses web portal 20 hours prior to departure and submits `PNR` + `LastName` to `P6.0`.
2. **Eligibility Validation**: `P6.0` verifies that departure is between T-24h and T-1h, and verifies PNR payment confirmation with `D5`.
3. **Flow 10 (QR Boarding Pass)**: `P6.0` creates a boarding pass entity in `D6`, encodes flight details into a digital 2D QR matrix with a cryptographically signed HMAC payload, and streams the digital boarding pass to E1.
4. **Flow 11 (Airport Gate Scan)**: Airport Gate Staff (E3) scans passenger's phone screen using an optical laser scanner at the boarding gate.
5. **Flow 12 (Gate Embarkation Clearance)**: `P6.0` decodes the QR token, verifies digital authenticity, verifies passenger has not already embarked, atomically updates boarding status to `EMBARKED` in `D6`, and flashes green clearance to E3.

### Cycle 3: Asynchronous Event Notification Dispatch (RabbitMQ AMQP)

1. **Event Publication**: Upon booking completion, `P5.0` serializes a `BookingConfirmedEvent` containing PNR, flight details, passenger names, and total fare, publishing it to RabbitMQ Exchange `aerobook.direct`.
2. **Queue Persistence**: Message broker (`D7`) buffers the payload in durable queue `booking.events`.
3. **Event Consumption**: Notification Worker (`P7.0`) consumes the event via AMQP protocol.
4. **Out-of-Band Delivery**: `P7.0` formats an HTML electronic ticket and dispatches calls to SendGrid (Email) and Twilio (SMS).
5. **Zero Latency Impact**: Passenger receives instantaneous REST response for booking without waiting for third-party email/SMS round-trips.

---

## 5. Security & Architectural Guarantees

1. **Zero PAN Storage**: Credit card numbers never touch AeroBook disk storage or databases. All transactions use opaque client-side payment tokens authorized directly with external gateways (PCI-DSS Scope Minimization).
2. **Database-Per-Service Isolation**: Strict microservice architectural isolation. Direct SQL cross-joins between databases (e.g., joining `bookings` with `users`) are architecturally barred, preventing catastrophic cascade locking.
3. **At-Least-Once Delivery**: RabbitMQ queues are marked as `durable: true` with persistent delivery mode. Acknowledgments (`basicAck`) are emitted only after notification dispatch succeeds. Failed messages are routed to `booking.dlq` for forensic analysis.
