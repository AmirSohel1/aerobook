# AeroBook Enterprise Platform - System Architecture Specification

> **Theme**: Dark Canvas Background (`#000000`), Crisp White Marker Strokes (`#FFFFFF`), Enterprise Microservices Blueprint.

---

## 1. High-Level Architecture Diagram (Mermaid B&W Theme)

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
    classDef clientBox fill:#000000,stroke:#ffffff,stroke-width:2px,color:#ffffff;
    classDef gatewayBox fill:#050505,stroke:#ffffff,stroke-width:2.5px,color:#ffffff;
    classDef eurekaBox fill:#050505,stroke:#ffffff,stroke-width:2px,stroke-dasharray:4 4,color:#ffffff;
    classDef serviceBox fill:#050505,stroke:#ffffff,stroke-width:2px,color:#ffffff;
    classDef queueBox fill:#050505,stroke:#ffffff,stroke-width:2px,stroke-dasharray:5 3,color:#ffffff;
    classDef dbCylinder fill:#000000,stroke:#ffffff,stroke-width:2px,color:#ffffff;

    subgraph CLIENTS ["LAYER 1: PRESENTATION & CLIENT TIER"]
        WebClient["Web SPA<br/>(React / Angular)"]:::clientBox
        MobileClient["Mobile App<br/>(iOS / Android)"]:::clientBox
        SwaggerUI["Swagger UI Hub<br/>(Dropdown Engine)"]:::clientBox
        AdminPortal["Admin Console<br/>(Operations & Staff)"]:::clientBox
    end

    subgraph EDGE_ROUTING ["LAYER 2: EDGE ROUTING & DISCOVERY TIER"]
        Gateway["fa:fa-shield-alt API Gateway Platform Hub (Port 8083)<br/>* Spring Cloud Gateway (Netty WebFlux)<br/>* AuthenticationFilter (JWT Validation)<br/>* RouteValidator (Public vs Secured Whitelist)<br/>* GatewayDocsController (Multi-Service Dropdown)"]:::gatewayBox
        Eureka["fa:fa-compass Eureka Registry<br/>(Port 8761)<br/>Heartbeat Probes<br/>Client-Side LB"]:::eurekaBox
    end

    subgraph CORE_SERVICES ["LAYER 3: CORE MICROSERVICES BUSINESS LOGIC TIER"]
        AuthSvc["1. auth-service (Port 8082)<br/>* BCrypt Hash & Credentials<br/>* JWT Bearer Token Issuer<br/>* Refresh Token Rotations"]:::serviceBox
        UserSvc["2. user-service (Port 8084)<br/>* Traveler Profile CRUD<br/>* Passport & Vault Store<br/>* Emergency Contacts"]:::serviceBox
        FlightSvc["3. flight-service (Port 8087)<br/>* Fleet Inventory (Boeing/Airbus)<br/>* Route Schedules & Gates<br/>* Real-Time Seat Locking"]:::serviceBox
        FareSvc["4. fare-service (Port 8089)<br/>* Dynamic Pricing Engine<br/>* Cabin Class Multipliers<br/>* Surge & Peak Algorithms"]:::serviceBox
        BookingSvc["5. booking-service (Port 8088)<br/>* Master Reservation Engine<br/>* 6-Char PNR Generation<br/>* OpenFeign Coordinator"]:::serviceBox
        CheckInSvc["6. check-in-service (Port 8090)<br/>* 24h Web Check-In<br/>* Interactive Seat Picking<br/>* QR Digital Boarding Pass"]:::serviceBox
    end

    subgraph MESSAGING ["LAYER 4: ASYNCHRONOUS EVENT BUS (Port 5672)"]
        RabbitMQ["fa:fa-envelope RabbitMQ Broker<br/>Topic Exchange: booking.exchange<br/>Queue: booking.events<br/>&rarr; Email Confirmation & SMS Alerts"]:::queueBox
    end

    subgraph PERSISTENCE ["LAYER 5: DATA PERSISTENCE (Database-per-Service MySQL 8.0)"]
        DB_Auth[("aerobook_auth_db<br/>Port 3306")]:::dbCylinder
        DB_User[("aerobook_user_db<br/>Port 3306")]:::dbCylinder
        DB_Flight[("aerobook_flight_db<br/>Port 3306")]:::dbCylinder
        DB_Fare[("aerobook_fare_db<br/>Port 3306")]:::dbCylinder
        DB_Booking[("aerobook_booking_db<br/>Port 3306")]:::dbCylinder
        DB_CheckIn[("aerobook_checkin_db<br/>Port 3306")]:::dbCylinder
    end

    %% Client Traffic to Gateway
    WebClient -->|HTTPS REST| Gateway
    MobileClient -->|HTTPS REST| Gateway
    SwaggerUI -->|OpenAPI Specs| Gateway
    AdminPortal -->|Admin Routes| Gateway

    %% Gateway to Eureka
    Gateway -.->|Service Lookup| Eureka

    %% Gateway to Microservices
    Gateway -->|/api/auth/**| AuthSvc
    Gateway -->|/api/v1/users/**| UserSvc
    Gateway -->|/api/flights/**| FlightSvc
    Gateway -->|/api/fares/**| FareSvc
    Gateway -->|/api/bookings/**| BookingSvc
    Gateway -->|/api/check-ins/**| CheckInSvc

    %% Inter-Service Feign Communication
    AuthSvc -.->|OpenFeign (Sync)| UserSvc
    BookingSvc -.->|OpenFeign: Lock Seat| FlightSvc
    BookingSvc -.->|OpenFeign: Verify Price| FareSvc
    CheckInSvc -.->|OpenFeign: Verify PNR| BookingSvc

    %% Asynchronous Messaging
    BookingSvc ==>|AMQP: BookingCreatedEvent| RabbitMQ

    %% Database-per-Service Isolation
    AuthSvc --- DB_Auth
    UserSvc --- DB_User
    FlightSvc --- DB_Flight
    FareSvc --- DB_Fare
    BookingSvc --- DB_Booking
    CheckInSvc --- DB_CheckIn
```

---

## 2. Architectural Principles & Patterns

### 1. API Gateway Pattern (`api-gateway`, Port 8083)

- **Single Entry Point**: All client requests (Web SPA, Mobile Apps, Swagger UI, Admin consoles) enter through port `8083`. No downstream microservice exposes public ports to internet clients.
- **Reactive Netty Non-Blocking Engine**: Handles high-concurrency request dispatching with minimal memory overhead compared to traditional thread-per-request servlet models.
- **Centralized Authentication & RBAC**:
  - `AuthenticationFilter`: Intercepts secured endpoints, extracts `Authorization: Bearer <token>`, validates signature against the JWT secret, and sets downstream HTTP headers (`X-User-Id`, `X-User-Role`).
  - `RouteValidator`: Whitelists open endpoints (`/api/auth/**`, `/api/gateway/**`, `/swagger-ui/**`, `**/v3/api-docs/**`) while strictly guarding business endpoints.
- **Unified OpenAPI 3 Documentation Hub**:
  - Integrates `GatewayDocsController` with a custom Swagger UI dropdown selector engine, serving service-scoped OpenAPI models to completely eliminate vertical scrolling.

### 2. Database-per-Service Pattern (MySQL 8.0, Port 3306)

- Each microservice owns an isolated, dedicated database schema:
  1. `aerobook_auth_db`: Credentials, password hashes, roles, refresh tokens.
  2. `aerobook_user_db`: Profiles, addresses, passport records, emergency contacts.
  3. `aerobook_flight_db`: Aircraft fleet, seat configurations, routes, departure schedules.
  4. `aerobook_fare_db`: Base pricing, class multipliers, dynamic surge rules.
  5. `aerobook_booking_db`: Confirmed bookings, passenger items, payment logs, 6-char PNRs.
  6. `aerobook_checkin_db`: Check-in records, assigned seats, QR boarding pass hashes.
- **Zero Cross-Database Coupling**: No foreign keys, table joins, or direct database connections exist between services. All inter-service data dependencies are resolved via REST OpenFeign APIs or RabbitMQ events.

### 3. Synchronous Inter-Service Communication (Spring Cloud OpenFeign)

- Microservices invoke neighboring domains synchronously using declarative OpenFeign REST clients:
  - `auth-service` $\rightarrow$ `user-service`: Creates passenger profile upon registration.
  - `booking-service` $\rightarrow$ `flight-service`: Atomically locks inventory seat for 10 minutes.
  - `booking-service` $\rightarrow$ `fare-service`: Validates price quote before charging payment.
  - `check-in-service` $\rightarrow$ `booking-service`: Verifies PNR validity and flight date within 24h.

### 4. Asynchronous Event-Driven Architecture (RabbitMQ, Port 5672)

- **Topic Exchange (`booking.exchange`)**: Decouples the user-facing booking transaction from downstream notification workers.
- When `booking-service` commits a reservation, it publishes `BookingCreatedEvent` with the PNR, customer email, and itinerary.
- Background workers consume from `booking.events` to send HTML confirmation receipts, SMS alerts, and telemetry logs without degrading client HTTP response latency.
