# AeroBook Enterprise Platform — Complete Project Documentation & Architectural Report

> **Document Type**: Comprehensive Technical Project Report & Architectural Whitepaper  
> **Target Audience**: Evaluators, Technical Examiners, Software Architects, and Engineering Leads  
> **System Classification**: Distributed Cloud-Native Microservices Platform (Spring Boot 3.2, Spring Cloud, MySQL 8, RabbitMQ, Docker)

---

## 1. Executive Summary & Project Inception

### 1.1 What is AeroBook?

**AeroBook** is an enterprise-grade, cloud-native airline reservation and flight operations platform engineered to deliver sub-second flight discovery, real-time dynamic pricing, atomic seat inventory locking, distributed booking saga coordination, self-service web check-in, and airport gate embarkation verification.

### 1.2 The Problem in Modern Aviation Systems

The global commercial aviation industry operates on tight scheduling margins, extreme concurrency during promotional fare flash sales, and mission-critical 99.999% availability requirements. Traditional monolithic airline reservation platforms suffer from severe structural bottlenecks:

1. **Flash-Sale Search Contention**: Search traffic (often 500x higher than booking traffic) saturates shared monolithic database connections, causing checkout cart timeouts and abandoned bookings.
2. **Double-Booking & Race Conditions**: High-concurrency seat selections on high-demand routes lead to double-selling seats unless protected by distributed atomic locks.
3. **Airport Boarding Deadlocks**: Departure gate scanners require sub-100ms response times. In a monolithic deployment, an unexpected surge in ticket search or fare recalculations degrades airport gate scanner performance, risking airport tarmac departure delays.
4. **PCI-DSS Compliance Exposure**: In monolithic architectures, storing or handling credit cards within the central database brings the entire monolithic codebase under intense PCI-DSS audit scrutiny.

### 1.3 The AeroBook Mission

AeroBook replaces legacy monolithic fragility with a **modular, distributed microservices architecture**. By enforcing strict **Database-per-Service isolation**, **non-blocking edge routing**, **event-driven asynchronous messaging**, and **client-side tokenized payments**, AeroBook achieves high availability, fault containment, linear horizontal scalability, and zero-cardholder data breach risk.

---

## 2. Architecture Showdown: Why Microservices & Not Monolithic?

### 2.1 The Monolithic Anatomy & Its Failure Modes

In a monolithic design, all functionality—authentication, customer profiles, flight search, fare calculations, seat inventory, booking payments, and airport check-in—runs as a single process (a giant WAR or JAR file) connected to a single central database (`aerobook_monolith_db`).

```
[ MONOLITHIC AIRLINE APPLICATION ]
├── Auth Module
├── Flight Search Module
├── Dynamic Pricing Module
├── Booking & Payment Module
├── Web Check-In Module
└── Notification Module
         │ (Single Point of Failure - Shared Connection Pool)
         ▼
[ SINGLE CENTRAL DATABASE (Shared Tables & Row Locks) ]
```

#### Monolithic Failure Scenarios:

- **The "Black Friday" Flash Sale Collapse**: When 100,000 users query holiday flights simultaneously, database CPU spikes to 100%. The shared connection pool exhausts. A passenger standing at Airport Gate 22 attempting to scan their boarding pass is denied entry because the check-in code cannot acquire a database connection.
- **Cascading Outages (Blast Radius = 100%)**: A memory leak or infinite loop in dynamic fare pricing crashes the entire JVM process. Flight search, existing bookings, and check-in services immediately drop offline.
- **Deployment Paralysis**: Modifying an airport baggage tax rule requires rebuilding, testing, and deploying the entire multi-gigabyte application, risking accidental regressions in the core booking flow.

---

### 2.2 Why AeroBook Chose Microservices

```
[ Web & Mobile Clients ]
         │ (HTTPS REST)
         ▼
[ Spring Cloud API Gateway (Port 8083) ] ── (Heartbeats) ── [ Netflix Eureka (Port 8761) ]
         │ (Stateless JWT Validation & Dynamic Routing)
 ┌───────┼───────────────────────────┬───────────────────────────┐
 ▼       ▼                           ▼                           ▼
[auth]  [user]                   [flight]                    [fare]
  │       │                           │                           │
  ▼       ▼                           ▼                           ▼
(D1-DB) (D2-DB)                     (D3-DB)                     (D4-DB)
                                     │                           │
                                     └───────────┬───────────────┘
                                                 ▼
                                        [booking-service (8088)] ◄──► [Payment Gateway (E4)]
                                                 │
                                     ┌───────────┴───────────────┐
                                     ▼                           ▼
                                  (D5-DB)                [RabbitMQ Broker (5672)]
                                                                 │ (AMQP Event Stream)
                                                                 ▼
                                                    [Notification Worker (P7.0)]
                                                                 │
                                                                 ▼
                                                    [Email / SMS Gateways (E5)]
```

#### Key Microservice Advantages in AeroBook:

1. **Blast Radius Containment**: If `fare-service` crashes due to an upstream pricing API timeout, `booking-service` falls back to base published fares. If `flight-service` search is under heavy load, `check-in-service` at airport gates operates with zero latency on its completely isolated database.
2. **Targeted Asymmetric Scalability**: Flight search operations exhibit a 200:1 read-to-write ratio compared to booking checkouts. In AeroBook, we can scale `flight-service` to 10 container instances while running only 2 instances of `booking-service`, saving massive cloud infrastructure costs.
3. **Database-Per-Service Isolation**: Each microservice encapsulates its own MySQL schema. There are zero cross-database SQL joins. Database contention on flight search queries cannot lock booking checkout tables.
4. **Autonomous Technology Evolution**: Individual services can be refactored, updated, or optimized independently without full-system regression risks.

---

### 2.3 Comprehensive Comparison: Microservices vs. Monolithic

| Dimension                  | Monolithic Architecture                                                               | AeroBook Microservices Architecture                                                                   | AeroBook Strategic Choice Rationale                                                             |
| :------------------------- | :------------------------------------------------------------------------------------ | :---------------------------------------------------------------------------------------------------- | :---------------------------------------------------------------------------------------------- |
| **System Blast Radius**    | **100% (Catastrophic)**. A failure in one module terminates the entire platform.      | **Isolated (<15%)**. A crash in dynamic pricing does not affect boarding gate operations.             | Critical for aviation. Airport gate embarkation must never fail due to ticket promotion surges. |
| **Database Contention**    | **High**. Search reads cause table/row lock contention on booking manifests.          | **Zero**. Database-per-service pattern isolates flight reads from booking writes.                     | Eliminates database bottlenecks during high-concurrency ticket booking rushes.                  |
| **Horizontal Scalability** | **Coarse-Grained**. Must scale the entire multi-gigabyte monolith as a whole.         | **Fine-Grained**. Scale `flight-service` 10x during sales while keeping `booking-service` at 2x.      | Optimizes cloud infrastructure costs and maximizes hardware resource utilization.               |
| **Deployment Agility**     | **Slow & High-Risk**. Requires full-system regression testing and scheduled downtime. | **Fast & Continuous**. Deploy `fare-service` updates independently without restarting other services. | Enables rapid business experimentation with pricing rules and dynamic surge algorithms.         |
| **Network Complexity**     | **Low**. In-process method calls within the same JVM memory space.                    | **Higher**. Incurs inter-service HTTP REST / AMQP network serialization hops.                         | AeroBook mitigates latency using non-blocking Netty WebFlux gateway and local memory caches.    |
| **Data Consistency**       | **Immediate (ACID)**. Single database provides traditional multi-table transactions.  | **Eventual Consistency**. Microservices coordinate via Saga pattern and RabbitMQ messaging.           | AeroBook uses two-phase atomic seat locks + asynchronous outbox event publishing.               |
| **Operational Overhead**   | **Simpler**. Single JAR/WAR to monitor, log, and deploy.                              | **Complex**. Requires service discovery, distributed tracing, and container orchestration.            | AeroBook standardizes deployment with Docker Compose, Eureka Discovery, and Actuator metrics.   |

---

## 3. Technology Stack: Deep "Why This & Not Others" Analysis

### 3.1 Backend Framework: Spring Boot 3.2 & Java 17

#### Why Spring Boot 3.2?

- **Production-Grade Enterprise Standards**: Built-in health checks, metric collection via Spring Boot Actuator, and standardized configuration management.
- **Spring Data JPA & Hibernate 6**: Mature Object-Relational Mapping (ORM), automated schema migration compatibility, type-safe criteria queries, and declarative transaction management (`@Transactional`).
- **Robust Cloud Ecosystem**: First-class support for Spring Cloud Gateway, OpenFeign declarative REST clients, and Eureka service discovery.
- **Modern Java 17 LTS**: Modern language features (Records for immutable DTOs, Pattern Matching, Sealed Classes, Garbage Collection optimizations with ZGC/G1).

#### Why Not Other Frameworks?

- **Why not Node.js / Express?**
  - _Single-Threaded Event Loop_: Heavy mathematical seat map evaluations and dynamic surge calculations can block the single thread, degrading throughput for concurrent connections.
  - _Lack of Enterprise Transaction Management_: Node.js lacks declarative transaction boundaries equivalent to Spring's `@Transactional`, requiring tedious manual database transaction rollback plumbing.
  - _Weak Compile-Time Guarantees_: Without heavy TypeScript boilerplate, JavaScript lacks the strict type safety required for high-value financial flight booking calculations.
- **Why not Python / Django?**
  - _Execution Latency & GIL_: Python's Global Interpreter Lock (GIL) limits multi-threaded CPU performance. Under high concurrency, Python services consume significantly more memory and produce higher tail latencies than Java 17.
  - _Immature Microservice Ecosystem_: Python lacks mature, native equivalents to Netflix Eureka, Spring Cloud Gateway, and Spring Cloud OpenFeign.
- **Why not Go (Golang)?**
  - _Excessive Boilerplate_: While Go offers exceptional raw speed and low memory usage, it lacks enterprise ORM maturity, declarative validation (`jakarta.validation`), and ready-made microservice governance frameworks. Building the same ecosystem in Go requires thousands of lines of custom networking and database plumbing.

---

### 3.2 Edge Routing & Security: Spring Cloud Gateway (Netty WebFlux)

#### Why Spring Cloud Gateway?

- **Non-Blocking Reactive Engine**: Built on Project Reactor and Netty. Instead of allocating one dedicated OS thread per HTTP request (which exhausts thread pools at ~2,000 concurrent connections), Netty handles tens of thousands of concurrent connections using an asynchronous event-loop model.
- **Global Authentication Filter**: Centralized security checkpoint (`AuthenticationFilter`). Every inbound request is inspected for a valid HMAC-SHA256 JWT Bearer token before traffic is routed to downstream services.
- **Centralized Swagger UI Aggregation**: `GatewayDocsController` dynamically discovers all microservices via Eureka and provides a unified interactive Swagger UI hub with a single dropdown.

#### Why Not Other Edge Routers?

- **Why not Netflix Zuul 1.x?** Zuul 1.x uses a traditional blocking servlet model where one slow backend service exhausts all gateway threads, bringing down routing for the entire system. Spring Cloud Gateway is fully non-blocking.
- **Why not NGINX / Kong?** While NGINX is blazing fast, configuring dynamic microservice routing, custom JWT authorization filters with Spring Security, and live Eureka heartbeat integration requires writing Lua scripts or proprietary Enterprise plugins. Spring Cloud Gateway allows pure Java routing, filter chains, and native Spring context integration.

---

### 3.3 Service Discovery: Netflix Eureka Registry (Port 8761)

#### Why Netflix Eureka?

- **Dynamic Service Registration**: Downstream microservices boot up on dynamic ports, register their IP and port with Eureka, and send periodic heartbeats (every 30 seconds). The API Gateway queries Eureka to route traffic without hardcoded IP addresses.
- **AP System (Availability over Consistency)**: In CAP theorem terms, Eureka prioritizes Availability over Consistency. During a temporary network partition, Eureka nodes remain functional and allow services to communicate using client-side cached registries (via Ribbon/Spring Cloud LoadBalancer).
- **Self-Preservation Mode**: If a network glitch causes temporary heartbeat dropouts, Eureka does not prematurely unregister healthy services, preventing catastrophic thundering-herd routing failures.

#### Why Not Other Discovery Tools?

- **Why not HashiCorp Consul or Apache ZooKeeper?** Consul and ZooKeeper are CP systems (Consistent / Partition-Tolerant). Under network partitions, they halt registration or election until quorum is achieved. A temporary network hiccup can take down all service routing.
- **Why not Kubernetes Native DNS?** Kubernetes DNS is excellent in pure cloud container clusters, but AeroBook was designed to run seamlessly both in lightweight local environments (Docker Compose / Developer Laptops) and multi-cloud Kubernetes clusters without mandating a heavy K8s control plane.

---

### 3.4 Relational Persistence: MySQL 8.0 (Database-Per-Service)

#### Why MySQL 8.0?

- **ACID Transaction Guarantees**: Essential for aviation reservations. When a ticket is issued and payment settled, passenger records, PNR numbers, and payment receipts must be committed atomically.
- **Pessimistic Row-Level Locking (`SELECT ... FOR UPDATE`)**: Enables atomic seat hold reservations. When Passenger A clicks "Seat 14B", MySQL locks that specific row, preventing Passenger B from claiming the same seat simultaneously.
- **InnoDB Storage Engine**: High-performance row-level locking, foreign key integrity within service boundaries, crash recovery, and multi-version concurrency control (MVCC).

#### Why Not Other Databases?

- **Why not MongoDB / Document NoSQL?** Document stores lack native multi-document ACID transactions across complex relational entities (flights, cabins, seats, passengers). Eventual consistency models risk overselling seats during high-traffic booking rushes.
- **Why not PostgreSQL?** While PostgreSQL is robust, MySQL 8.0 provides lower memory footprint per container instance when running 6 separate database containers simultaneously on developer machines and cost-effective staging environments, while offering identical row-locking capabilities.

---

### 3.5 Asynchronous Event Messaging: RabbitMQ (AMQP 0-9-1)

#### Why RabbitMQ?

- **Push-Based Message Delivery**: Perfect for worker queues. As soon as a booking is completed, RabbitMQ pushes the `BookingConfirmedEvent` to the notification consumer worker.
- **Advanced Routing Topologies**: Supports Direct, Topic, and Fanout exchanges. AeroBook uses exchange `aerobook.direct` with routing keys like `booking.created` and `booking.cancelled`.
- **Guaranteed Delivery & DLX**: Message persistence (`durable: true`), manual consumer acknowledgments (`basicAck`), and Dead-Letter Exchanges (DLX) ensure that zero booking confirmation emails are lost during network glitches.

#### Why Not Apache Kafka?

- **Kafka is Built for Massive Stream Log Processing**: Kafka excels at ingesting millions of telemetry metrics or clickstreams per second where messages are read via consumer pull offsets.
- **RabbitMQ is Far Superior for Transactional Task Queues**: Airline booking events require complex routing, individual message acknowledgments, dead-letter re-routing, and instantaneous push dispatch. RabbitMQ has zero cluster configuration complexity compared to Kafka's ZooKeeper/KRaft requirements.

---

## 4. Service-by-Service Deep Dive

### 4.1 Service Overview Matrix

| Service Name           | Port | Database              | Primary Responsibility                   | Key Technologies                         |
| :--------------------- | :--- | :-------------------- | :--------------------------------------- | :--------------------------------------- |
| **`eureka-server`**    | 8761 | None (In-Memory)      | Service Discovery & Registry             | Spring Cloud Netflix Eureka Server       |
| **`api-gateway`**      | 8083 | None (Stateless)      | Edge Routing, JWT Security, Swagger Hub  | Spring Cloud Gateway, Netty WebFlux, JWT |
| **`auth-service`**     | 8082 | `aerobook_auth_db`    | User Authentication & Token Issuance     | Spring Security, BCrypt, HMAC-SHA256     |
| **`user-service`**     | 8084 | `aerobook_user_db`    | Passenger Profiles & Passports           | Spring Data JPA, Hibernate, MySQL        |
| **`flight-service`**   | 8087 | `aerobook_flight_db`  | Fleet, Schedules, Seat Inventory Locks   | Spring Data JPA, Pessimistic Locking     |
| **`fare-service`**     | 8089 | `aerobook_fare_db`    | Dynamic Pricing & Demand Surge Rules     | Spring Boot, BigDecimal Math Engine      |
| **`booking-service`**  | 8088 | `aerobook_booking_db` | Transactional PNR Orchestration (Core)   | Spring Cloud OpenFeign, RabbitMQ AMQP    |
| **`check-in-service`** | 8090 | `aerobook_checkin_db` | Web Check-In & 2D QR Code Pass Generator | ZXing QR Generator, Digital Signatures   |

---

### 4.2 Detailed Breakdown of Each Microservice

#### 1. `eureka-server` (Port 8761)

- **Purpose**: Centralized registry maintaining a live catalog of all running microservice instances.
- **Working**: When any microservice boots up, it broadcasts its service ID, IP address, and dynamic port to Eureka. Services exchange heartbeats every 30 seconds.
- **Why Isolated**: Prevents hardcoding IP addresses, enabling zero-downtime rolling deployments and dynamic autoscaling.

#### 2. `api-gateway` (Port 8083)

- **Purpose**: The single entry point for all external traffic (Web, Mobile, Admin).
- **Working**:
  - Receives HTTP requests on port 8083.
  - Evaluates URL against `RouteValidator`. Public routes (e.g., `/api/auth/login`, `/api/flights/search`, `/v3/api-docs/**`) bypass authentication.
  - Secured routes pass through `AuthenticationFilter`, which verifies the `Authorization: Bearer <token>` header, decodes the HMAC-SHA256 signature, extracts user claims, and forwards the request.
  - `GatewayDocsController` queries Eureka and aggregates OpenAPI 3.0 documentation into a single unified interactive Swagger dropdown hub.
- **Why Isolated**: Shields internal microservices from direct public Internet exposure, centralizing SSL termination, CORS policies, rate limiting, and authentication.

#### 3. `auth-service` (Port 8082)

- **Purpose**: Centralized identity and access management provider.
- **Working**:
  - User registration: Salts and hashes plaintext passwords using BCrypt (strength 10), persisting user accounts in `aerobook_auth_db`.
  - User login: Verifies credentials, generates a signed stateless JWT containing username, role claims (`ROLE_PASSENGER`, `ROLE_ADMIN`), and a 24-hour expiration timestamp.
- **Why Isolated**: Keeps sensitive credential handling completely segregated from business domain services. If booking service is breached, credential hashes remain safely behind isolated network boundaries.

#### 4. `user-service` (Port 8084)

- **Purpose**: Manages customer profiles, travel documentation, addresses, and emergency contacts.
- **Working**:
  - Stores traveler identity, government passport identifiers, nationality, and frequent flyer mileage.
  - Enforces international passport validation rules and GDPR-compliant profile rectification.
- **Why Isolated**: Encapsulates Personally Identifiable Information (PII) to comply with international data privacy laws (GDPR, CCPA).

#### 5. `flight-service` (Port 8087)

- **Purpose**: Flight catalog search, aircraft fleet management, and real-time seat inventory.
- **Working**:
  - High-speed indexed queries on `(origin_airport, destination_airport, departure_time)`.
  - Seat Inventory Management: Generates aircraft cabin seat maps (Economy, Premium Economy, Business, First).
  - **Atomic Seat Hold Algorithm**: Implements two-phase seat holds. When a user selects a seat during checkout, the seat status transitions from `AVAILABLE` to `HELD` with an expiration timestamp of 10 minutes. If checkout is not completed within 10 minutes, a scheduled background worker resets the seat to `AVAILABLE`.
- **Why Isolated**: Allows independent read-scaling. Flight search handles 200x more traffic than booking checkout; isolating it prevents search query spikes from degrading transactional booking databases.

#### 6. `fare-service` (Port 8089)

- **Purpose**: Real-time dynamic pricing and surge calculation engine.
- **Working**:
  - Computes final ticket prices using algorithm:
    $$\text{FinalPrice} = \text{BaseFare} \times \text{CabinMultiplier} \times (1 + \text{DemandSurgeRate}) + \text{AirportTaxes}$$
  - Dynamically evaluates cabin occupancy percentage to calculate demand surge:
    - 0% - 50% Occupancy: 0% Surge
    - 51% - 80% Occupancy: +15% Surge
    - 81% - 100% Occupancy: +35% Surge
  - Uses arbitrary-precision `BigDecimal` arithmetic to prevent binary floating-point financial drift.
- **Why Isolated**: Allows airline revenue managers to update dynamic pricing models, seasonal discounts, and fuel surcharges instantly without redeploying flight catalog or booking code.

#### 7. `booking-service` (Port 8088 - System Core)

- **Purpose**: The mission-critical transactional coordinator and PNR orchestration engine.
- **Working**:
  - Receives multi-passenger booking requests.
  - Coordinates with `flight-service` via OpenFeign to confirm atomic seat hold.
  - Coordinates with `fare-service` via OpenFeign to obtain the locked fare quote.
  - Calls external Payment Gateway (`E4`) using client-side payment tokens.
  - Generates an unambiguous, cryptographically random **6-character alphanumeric PNR** (Passenger Name Record, e.g., `AB74K2`) excluding ambiguous characters (`0`, `O`, `1`, `I`).
  - Commits booking and passenger entities to `aerobook_booking_db`.
  - Emits `BookingConfirmedEvent` to RabbitMQ exchange `aerobook.direct`.
- **Why Isolated**: Protects the core financial transaction revenue pipeline. Requires strict ACID transaction isolation and dedicated database hardware.

#### 8. `check-in-service` (Port 8090)

- **Purpose**: Airport web check-in, digital boarding pass issuance, and gate embarkation clearance.
- **Working**:
  - Enforces strict departure window rules: Check-in opens exactly 24 hours prior to flight departure ($T-24\text{h}$) and closes 1 hour prior to departure ($T-1\text{h}$).
  - Verifies PNR validity and payment confirmation with `booking-service`.
  - Generates digital boarding pass containing seat allocation, boarding sequence number, and a **Base64-encoded 2D QR barcode**.
  - QR Code payload contains: PNR, passenger name, flight number, seat number, gate, and an HMAC-SHA256 digital signature to prevent boarding pass tampering or counterfeit reproduction.
  - Airport Gate Staff Scanner API: Airport gate scanners submit scanned QR codes. The service validates the digital signature, verifies the passenger has not already boarded, atomically updates status to `EMBARKED`, and returns an immediate green clearance signal.
- **Why Isolated**: Airport gate scanners must operate with zero latency (sub-100ms) and 99.999% uptime even if the public booking website is undergoing maintenance.

---

## 5. Security & Data Governance Architecture

### 5.1 Zero PAN Storage (PCI-DSS Compliance)

Under PCI-DSS Level 1 compliance regulations, any database storing 16-digit Primary Account Numbers (PAN) or CVVs requires expensive hardware security modules (HSM), quarterly forensic audits, and strict network segmentation.

- **The AeroBook Architecture**: Front-end web and mobile clients communicate directly with third-party payment gateways (Stripe / Razorpay) via client-side SDKs. The payment gateway returns an opaque, single-use payment token (e.g., `tok_visa_4242`).
- AeroBook's `booking-service` transmits this token to the gateway API for settlement and only receives a non-sensitive transaction reference (`txn_89410984123`).
- **Zero credit card numbers ever touch AeroBook memory, logs, or MySQL database disks**, reducing PCI-DSS audit scope to the absolute minimum.

### 5.2 Stateless JWT Bearer Token Security

1. Client submits credentials to `POST /api/auth/login`.
2. `auth-service` validates BCrypt hash and signs an HMAC-SHA256 JWT containing `sub` (username), `roles` (`ROLE_PASSENGER`), `iat`, and `exp`.
3. Subsequent client requests include `Authorization: Bearer <token>`.
4. `api-gateway` validates the cryptographic signature using the shared secret key in memory. Downstream services receive pre-authenticated claims without querying the database for every HTTP request.

### 5.3 Concurrency Control & Double-Booking Prevention

- Problem: Two users simultaneously attempt to book the last available aisle seat (`14B`).
- Solution: AeroBook implements a two-phase reservation protocol using pessimistic locking:
  ```sql
  SELECT * FROM seat_inventories
  WHERE flight_id = :flightId AND seat_number = '14B'
  FOR UPDATE;
  ```
- The first transaction acquires an exclusive row lock, verifies `status = 'AVAILABLE'`, transitions status to `'HELD'`, sets `hold_expires_at = NOW() + 10 MINUTES`, and commits.
- The second concurrent transaction is blocked until the first commits, whereupon it reads `status = 'HELD'` and is immediately rejected with HTTP 409 Conflict.

---

## 6. Future Scope & Evolutionary Roadmap

1. **AI/ML Predictive Dynamic Fare Pricing**:
   - Integrate Python machine learning microservice (FastAPI + XGBoost/TensorFlow) reading historical booking velocities, seasonal holiday calendars, competitor fare web scrapers, and macroeconomic indicators to optimize revenue per seat kilometer (RASK).
2. **Biometric Airport Gate Embarkation**:
   - Facial recognition cameras installed at airport boarding gates capturing facial biometric embeddings, matched in real time against encrypted passport photo vectors stored in `user-service`.
3. **IATA ONE Order & NDC Multi-Carrier Interlining**:
   - Transition from legacy PNR/e-ticket data structures to IATA's modern ONE Order XML/JSON standard, enabling seamless booking of multi-leg itineraries spanning partner airlines with automated baggage RFID tracking.
4. **Mobile Apple Wallet & Google Wallet Pass Integration**:
   - Direct generation of PKPass (iOS) and Google Wallet JWT bundles containing dynamic push-updated flight departure gate changes and delay notifications.

---

## 7. Technical Defense & Viva Examination FAQ

### Q1: Why did you choose Database-per-Service instead of a single shared database with different tables?

**Answer**: A shared database destroys microservice autonomy. In a shared database, multiple services compete for database connection pools, memory caches, and disk I/O. A spike in flight catalog searches would cause table locks affecting booking checkout commits. Furthermore, Database-per-Service allows each database to be tuned, backed up, and scaled independently without risking cross-service schema corruption.

### Q2: How do you handle data consistency without cross-database foreign keys?

**Answer**: In microservices, immediate ACID consistency across multiple databases is impossible without distributed two-phase commit (2PC) protocols, which introduce severe performance bottlenecks and single points of failure. AeroBook uses **Eventual Consistency** guided by the **Saga Pattern**: `booking-service` holds seats via REST, commits the booking, and emits an event to RabbitMQ. The notification service consumes the event asynchronously. If payment fails, compensating transactions release the held seats immediately.

### Q3: Why is RabbitMQ used instead of synchronous HTTP REST calls for sending email and SMS notifications?

**Answer**: Third-party email (SendGrid) and SMS (Twilio) APIs typically have response latencies of 500ms to 3,000ms. If `booking-service` called these APIs synchronously within the checkout HTTP request, the user would experience slow checkout times, and any network timeout in SendGrid would cause the booking transaction to fail. By publishing `BookingConfirmedEvent` to RabbitMQ in under 5ms, the user receives an instantaneous booking response while notification workers process deliveries in the background.

### Q4: Why is Spring Cloud Gateway built on Netty WebFlux instead of traditional Tomcat Servlets?

**Answer**: Traditional Tomcat servlets allocate one dedicated thread per HTTP request. When concurrent users exceed thread pool limits (~200–500 threads), the server exhausts memory and rejects new connections. Netty WebFlux uses non-blocking event loops, where a small number of threads handle tens of thousands of concurrent connections asynchronously, providing high resilience at the edge.

### Q5: How is the 6-character PNR generated, and how do you guarantee uniqueness?

**Answer**: AeroBook's `PnrGenerator` uses `SecureRandom` to generate a 6-character uppercase alphanumeric string selected from a 32-character alphabet that excludes easily confused characters (`0`, `O`, `1`, `I`). Uniqueness is enforced at the database level via a `UNIQUE` index constraint on `bookings.pnr`. In the statistically rare event of a collision, the service catches the constraint violation and transparently regenerates a new PNR.
