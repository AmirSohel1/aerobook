# AeroBook Enterprise Platform - System Use Case Diagram & Specification

> **Theme**: Black Canvas Background (`#000000`), Crisp White Marker Strokes (`#FFFFFF`), UML 2.5 Standard Notation.

---

## 1. High-Level UML Use Case Diagram (Black & White Theme)

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
    'tertiaryColor': '#000000',
    'actorBkg': '#000000',
    'actorBorder': '#ffffff',
    'actorTextColor': '#ffffff',
    'actorLineColor': '#ffffff'
  }
}}%%
graph LR
    classDef actorNode fill:#000000,stroke:#ffffff,stroke-width:2px,color:#ffffff,stroke-dasharray:none;
    classDef useCase fill:#000000,stroke:#ffffff,stroke-width:2px,color:#ffffff,rx:25px,ry:25px;
    classDef includeCase fill:#050505,stroke:#ffffff,stroke-width:1.5px,stroke-dasharray:4 4,color:#ffffff,rx:20px,ry:20px;
    classDef boundary fill:#000000,stroke:#ffffff,stroke-width:2px,stroke-dasharray:6 4,color:#ffffff;
    classDef extSystem fill:#0a0a0a,stroke:#ffffff,stroke-width:2px,color:#ffffff;

    subgraph ACTORS_LEFT [" "]
        Passenger["fa:fa-user Passenger<br/>(Customer)"]:::actorNode
    end

    subgraph AEROBYTE_SYSTEM ["SYSTEM BOUNDARY: AeroBook Airline Platform"]

        subgraph AUTH_USER ["Identity & Profile (Ports 8082, 8084)"]
            UC1(["UC-01: Register New Account"]):::useCase
            UC2(["UC-02: Login & Authenticate"]):::useCase
            UC3(["UC-03: Manage Profile & Info"]):::useCase
            UC_JWT(["UC-02a: Verify JWT Token"]):::includeCase
        end

        subgraph FLIGHT_FARE ["Flight Discovery & Fares (Ports 8087, 8089)"]
            UC4(["UC-04: Search Flights & Schedules"]):::useCase
            UC5(["UC-05: View Dynamic Fares"]):::useCase
            UC_SEAT_AVAIL(["UC-04a: Check Seat Inventory"]):::includeCase
            UC_DYNAMIC_PRICING(["UC-05a: Calculate Surge Multipliers"]):::includeCase
        end

        subgraph BOOKING_NOTIF ["Reservations & Messaging (Port 8088)"]
            UC6(["UC-06: Book Flight Ticket"]):::useCase
            UC7(["UC-07: Cancel Booking"]):::useCase
            UC_LOCK(["UC-06a: Lock Seat"]):::includeCase
            UC_PAY(["UC-06b: Process Payment"]):::includeCase
            UC_PNR(["UC-06c: Generate 6-Char PNR"]):::includeCase
            UC_NOTIF(["UC-06d: Publish RabbitMQ Event"]):::includeCase
            UC_RELEASE(["UC-07a: Release Seat to Inventory"]):::includeCase
        end

        subgraph CHECKIN_BOARDING ["Airport Check-In & Gate (Port 8090)"]
            UC8(["UC-08: Online Web Check-In"]):::useCase
            UC9(["UC-09: Select Seat"]):::useCase
            UC10(["UC-10: Generate Digital Boarding Pass"]):::useCase
            UC11(["UC-11: Validate Boarding at Gate"]):::useCase
        end

        subgraph ADMIN_OPS ["Fleet & Schedule Admin (Ports 8083, 8087, 8089)"]
            UC12(["UC-12: Manage Aircraft Fleet"]):::useCase
            UC13(["UC-13: Manage Flight Schedules"]):::useCase
            UC14(["UC-14: Configure Fare Rules"]):::useCase
            UC15(["UC-15: Monitor Gateway Metrics"]):::useCase
        end
    end

    subgraph ACTORS_RIGHT [" "]
        Admin["fa:fa-user-tie Airline Administrator<br/>(ROLE_ADMIN)"]:::actorNode
        Staff["fa:fa-id-badge Gate Agent / Staff<br/>(ROLE_STAFF)"]:::actorNode
        PaymentSys["External Payment Gateway"]:::extSystem
        RabbitBroker["RabbitMQ Event Broker"]:::extSystem
    end

    %% Passenger Associations
    Passenger --- UC1
    Passenger --- UC2
    Passenger --- UC3
    Passenger --- UC4
    Passenger --- UC5
    Passenger --- UC6
    Passenger --- UC7
    Passenger --- UC8
    Passenger --- UC9
    Passenger --- UC10

    %% Administrator Associations
    Admin --- UC12
    Admin --- UC13
    Admin --- UC14
    Admin --- UC15

    %% Staff Associations
    Staff --- UC11
    Staff --- UC8

    %% Include Relationships
    UC2 -. "<<include>>" .-> UC_JWT
    UC4 -. "<<include>>" .-> UC_SEAT_AVAIL
    UC5 -. "<<include>>" .-> UC_DYNAMIC_PRICING
    UC6 -. "<<include>>" .-> UC_LOCK
    UC6 -. "<<include>>" .-> UC_PAY
    UC6 -. "<<include>>" .-> UC_PNR
    UC6 -. "<<include>>" .-> UC_NOTIF
    UC7 -. "<<include>>" .-> UC_RELEASE
    UC8 -. "<<include>>" .-> UC9
    UC8 -. "<<include>>" .-> UC10

    %% External System Interactions
    UC_PAY --- PaymentSys
    UC_NOTIF --- RabbitBroker
```

---

## 2. Actors Specification

| #   | Actor                         | Type             | Description & Responsibilities                                                                                                                 | Security Role                |
| --- | ----------------------------- | ---------------- | ---------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------- |
| 1   | **Passenger (Traveler)**      | Primary Human    | Books flights, views dynamic fares, manages reservations, performs online check-in, downloads digital QR boarding passes.                      | `ROLE_PASSENGER` / Anonymous |
| 2   | **Airline Administrator**     | Primary Human    | Manages aircraft fleet inventory, configures flight routes and departure schedules, configures dynamic pricing rules, inspects gateway health. | `ROLE_ADMIN`                 |
| 3   | **Airport Gate Staff**        | Primary Human    | Inspects passenger credentials at the boarding gate, verifies digital boarding pass QR codes, marks passengers as `BOARDED`.                   | `ROLE_STAFF`                 |
| 4   | **External Payment Gateway**  | Secondary System | Processes credit/debit card transactions during ticket reservation and executes refunds upon cancellation.                                     | External Service API         |
| 5   | **RabbitMQ Messaging Broker** | Secondary System | Asynchronous message broker delivering booking confirmations, payment events, and departure alert notifications.                               | AMQP Broker (Port 5672)      |

---

## 3. Use Case Catalog & Microservice Traceability

| Use Case ID | Use Case Name             | Primary Actor | Description                                                                                | Target Microservice & Endpoint                                 |
| ----------- | ------------------------- | ------------- | ------------------------------------------------------------------------------------------ | -------------------------------------------------------------- |
| **UC-01**   | Register New Account      | Passenger     | Creates a traveler account with hashed BCrypt credentials and role assignment.             | `auth-service` & `user-service`<br/>`POST /api/auth/register`  |
| **UC-02**   | Login & Authenticate      | All Actors    | Validates credentials and returns an encrypted JWT Bearer access token.                    | `auth-service`<br/>`POST /api/auth/login`                      |
| **UC-03**   | Manage Profile & Info     | Passenger     | Updates passport number, contact phone, residential address, and preferences.              | `user-service`<br/>`GET/PUT /api/v1/users/{id}`                |
| **UC-04**   | Search Flights & Routes   | Passenger     | Queries available flights by origin airport, destination airport, and date.                | `flight-service`<br/>`GET /api/flights/search`                 |
| **UC-05**   | View Dynamic Fares        | Passenger     | Calculates real-time price quotes based on seat class, demand surge, and dates.            | `fare-service`<br/>`POST /api/fares/calculate`                 |
| **UC-06**   | Book Flight Ticket        | Passenger     | Reserves seat, processes payment, generates a 6-character PNR, and publishes notification. | `booking-service`<br/>`POST /api/bookings`                     |
| **UC-07**   | Cancel Booking            | Passenger     | Cancels reservation, initiates refund request, and releases locked seat back to inventory. | `booking-service`<br/>`DELETE /api/bookings/{id}`              |
| **UC-08**   | Online Web Check-In       | Passenger     | Performs web check-in within 24h of flight departure using PNR and passenger name.         | `check-in-service`<br/>`POST /api/check-ins`                   |
| **UC-09**   | Select Seat               | Passenger     | Chooses preferred seat (Window, Aisle, Middle, Extra Legroom) from seat matrix.            | `check-in-service`<br/>`POST /api/check-ins/select-seat`       |
| **UC-10**   | Generate Boarding Pass    | Passenger     | Generates digital boarding pass containing security verification QR code string.           | `check-in-service`<br/>`GET /api/check-ins/{id}/boarding-pass` |
| **UC-11**   | Validate Boarding at Gate | Gate Staff    | Scans QR code or enters check-in ID at boarding gate to mark passenger as BOARDED.         | `check-in-service`<br/>`PUT /api/check-ins/{id}/board`         |
| **UC-12**   | Manage Aircraft Fleet     | Admin         | Registers new aircraft (Boeing, Airbus), configures seat capacities and models.            | `flight-service`<br/>`POST/GET /api/admin/aircrafts`           |
| **UC-13**   | Manage Flight Schedules   | Admin         | Creates new flight routes, assigns aircraft, departure/arrival times, and status.          | `flight-service`<br/>`POST/GET /api/admin/flights`             |
| **UC-14**   | Configure Fare Rules      | Admin         | Establishes base pricing, surge coefficients, baggage fees, and cabin multipliers.         | `fare-service`<br/>`POST/PUT /api/fares`                       |
| **UC-15**   | Monitor Gateway Metrics   | Admin         | Inspects real-time routing health, microservice status directory, and RBAC matrix.         | `api-gateway`<br/>`GET /api/gateway/health`                    |

---

## 4. Detailed Use Case Specifications (Core Flows)

### UC-06: Book Flight Ticket (`<<include>>` Cascade)

- **Primary Actor**: Passenger
- **Preconditions**:
  1. Passenger is authenticated with valid JWT token.
  2. Selected flight has available seats in the chosen cabin class.
- **Main Success Scenario**:
  1. Passenger submits flight number, travel date, passenger information, and chosen seat class.
  2. System locks requested seat in `flight-service` (`<<include>> UC-06a`).
  3. System calculates verified fare through `fare-service`.
  4. System processes payment transaction (`<<include>> UC-06b`).
  5. System generates an immutable, collision-free 6-character alphanumeric PNR (e.g. `AB7K9Q`) (`<<include>> UC-06c`).
  6. System creates booking record with status `CONFIRMED`.
  7. System publishes `BookingCreatedEvent` to RabbitMQ queue `booking.events` (`<<include>> UC-06d`).
  8. Passenger receives booking summary and PNR receipt.
- **Extensions / Exceptions**:
  - _2a. Seat unavailable_: Booking fails with `409 Conflict - No seats available`.
  - _4a. Payment declined_: Locked seat is unlocked and booking is aborted.

---

### UC-08: Online Web Check-In & Boarding Pass

- **Primary Actor**: Passenger
- **Preconditions**:
  1. Booking status is `CONFIRMED`.
  2. Departure is within 24 hours.
- **Main Success Scenario**:
  1. Passenger enters PNR and Last Name.
  2. System verifies booking status with `booking-service`.
  3. Passenger selects seat from cabin layout (`<<include>> UC-09`).
  4. System marks check-in status as `CHECKED_IN`.
  5. System creates digital Boarding Pass with unique QR hash string (`<<include>> UC-10`).
  6. Passenger downloads boarding pass.
