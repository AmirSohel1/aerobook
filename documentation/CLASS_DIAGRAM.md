# AeroBook Enterprise Platform - UML Class Diagram & Data Model Specification

> **Theme**: Dark Canvas Background (`#000000`), Crisp White Marker Strokes (`#FFFFFF`), UML 2.5 Standard Notation.

---

## 1. High-Level UML Class Diagram (Mermaid B&W Theme)

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
classDiagram
    direction TB

    class Credential {
        -Long credentialId
        -Long userId
        -String email
        -String password
        -Role role
        -LocalDateTime createdAt
        -LocalDateTime updatedAt
        +verifyPassword(raw : String) Boolean
        +updatePassword(newHash : String) void
        +getRole() Role
    }

    class Role {
        <<enumeration>>
        ROLE_PASSENGER
        ROLE_ADMIN
        ROLE_STAFF
    }

    class RefreshToken {
        -Long id
        -String token
        -Instant expiryDate
        -Credential credential
        +isExpired() Boolean
        +verifyExpiration() RefreshToken
    }

    class UserEntity {
        -Long userId
        -String firstName
        -String lastName
        -String email
        -String phone
        -String address
        -String passportNumber
        -LocalDate dateOfBirth
        -String role
        +getFullName() String
        +updateProfile(dto : UserRequest) void
        +validatePassport() Boolean
    }

    class Aircraft {
        -Long id
        -String model
        -Integer capacity
        -Integer totalRows
        -Integer seatsPerRow
        +calculateTotalCapacity() Integer
        +getSeatMap() Map
    }

    class Flight {
        -Long id
        -String flightNumber
        -String airlineName
        -String source
        -String destination
        -LocalDateTime departureTime
        -Integer availableSeats
        -BigDecimal basePrice
        -FlightStatus status
        +lockSeat() Boolean
        +releaseSeat() void
        +hasAvailability() Boolean
    }

    class FlightStatus {
        <<enumeration>>
        SCHEDULED
        DELAYED
        BOARDING
        DEPARTED
        CANCELLED
    }

    class Fare {
        -Long id
        -Long flightId
        -BigDecimal economyFare
        -BigDecimal businessFare
        -BigDecimal firstClassFare
        -BigDecimal taxPercentage
        -BigDecimal discountPercentage
        -String currency
        +calculateTotalFare(class : String) BigDecimal
        +applyDiscount(code : String) BigDecimal
        +applySurge(multiplier : BigDecimal) void
    }

    class Booking {
        -Long bookingId
        -String pnr
        -Long userId
        -Long flightId
        -LocalDateTime bookingDate
        -BigDecimal totalFare
        -BookingStatus status
        -List~Passenger~ passengers
        +addPassenger(p : Passenger) void
        +confirmReservation() void
        +cancelReservation() void
        +calculatePassengerTotal() BigDecimal
    }

    class BookingStatus {
        <<enumeration>>
        BOOKED
        CONFIRMED
        CANCELLED
    }

    class Passenger {
        -Long passengerId
        -String firstName
        -String lastName
        -Integer age
        -String gender
        -String seatNumber
        -Booking booking
        +assignSeat(seat : String) void
        +getPassengerInfo() String
    }

    class CheckIn {
        -Long id
        -Long bookingId
        -String passengerName
        -String seatNumber
        -String boardingPassNumber
        -LocalDateTime checkInTime
        -String gate
        -String status
        +assignSeat(seat : String) void
        +generateBoardingPassNumber() String
        +markBoarded() void
    }

    class PnrGenerator {
        <<Utility>>
        -String CHAR_POOL
        -SecureRandom RANDOM
        +generatePnr() String
    }

    class ErrorResponse {
        <<DTO>>
        -LocalDateTime timestamp
        -Integer status
        -String error
        -String message
        +getFormattedMessage() String
    }

    %% Relationships
    Credential "1" *-- "0..*" RefreshToken : composition
    Credential ..> Role : depends
    Credential ..> UserEntity : logical 1:1 (userId)
    Aircraft "1" o-- "0..*" Flight : aggregation
    Flight ..> FlightStatus : depends
    Flight ..> Fare : logical 1:1 (flightId)
    Booking "1" *-- "1..*" Passenger : composition
    Booking ..> BookingStatus : depends
    Booking ..> PnrGenerator : utilizes
    Booking ..> Flight : logical reference (flightId)
    Booking ..> CheckIn : logical 1:1 (bookingId)
```

---

## 2. Core Domain Entity Catalog

| Entity Name        | Primary Key           | Owning Microservice       | Dedicated Database Table | Key Attributes & Capabilities                                                                     |
| ------------------ | --------------------- | ------------------------- | ------------------------ | ------------------------------------------------------------------------------------------------- |
| **`Credential`**   | `credentialId : Long` | `auth-service (8082)`     | `credentials`            | BCrypt password hash, user email index, role authority (`ROLE_PASSENGER`, `ROLE_ADMIN`).          |
| **`RefreshToken`** | `id : Long`           | `auth-service (8082)`     | `refresh_tokens`         | UUID session token, instant expiry, cascading deletion with parent credential.                    |
| **`UserEntity`**   | `userId : Long`       | `user-service (8084)`     | `users`                  | Traveler personal details, passport number, phone, address, and emergency contact coordinates.    |
| **`Aircraft`**     | `id : Long`           | `flight-service (8087)`   | `aircrafts`              | Airplane fleet model (Boeing 737, Airbus A320), seat rows, seats per row, and total capacity.     |
| **`Flight`**       | `id : Long`           | `flight-service (8087)`   | `flights`                | Flight number (e.g. `AB-204`), origin airport, destination, schedule, and atomic seat locking.    |
| **`Fare`**         | `id : Long`           | `fare-service (8089)`     | `fares`                  | Economy, Business, and First Class pricing tiers, tax percentages, and dynamic surge multipliers. |
| **`Booking`**      | `bookingId : Long`    | `booking-service (8088)`  | `bookings`               | Unique 6-character PNR (`AB7K9Q`), total fare, booking status, and child passenger collection.    |
| **`Passenger`**    | `passengerId : Long`  | `booking-service (8088)`  | `passengers`             | Traveler demographic, allocated seat number (e.g. `12A`), and foreign key to parent booking.      |
| **`CheckIn`**      | `id : Long`           | `check-in-service (8090)` | `check_in`               | Boarding pass identifier, seat number, gate assignment, and boarding status (`BOARDED`).          |

---

## 3. Relationship Types & Multiplicities Explained

### 1. Composition (`*--`)

- **`Booking` $\rightarrow$ `Passenger` (1 to 1..\*)**:
  A passenger record cannot exist in isolation without a parent booking reservation. In JPA, `Booking` specifies `CascadeType.ALL` and `orphanRemoval = true`. If a booking is deleted, all attached passengers are automatically purged.
- **`Credential` $\rightarrow$ `RefreshToken` (1 to 0..\*)**:
  A refresh token strictly belongs to one user credential account. If the credential is deleted, all active session tokens are revoked and deleted.

### 2. Aggregation (`o--`)

- **`Aircraft` $\rightarrow$ `Flight` (1 to 0..\*)**:
  An airplane can operate multiple flights over its lifetime. However, if a specific flight schedule is cancelled, the underlying physical aircraft continues to exist.

### 3. Logical Cross-Microservice References (Dashed Dependencies `..>`)

- Because AeroBook follows the **Database-per-Service** architectural pattern, entities in different microservices do NOT share foreign key constraints.
- Instead, services hold logical identifier references:
  - `Booking` stores `userId` (pointing to `UserEntity`) and `flightId` (pointing to `Flight`).
  - `CheckIn` stores `bookingId` (pointing to `Booking`).
  - `Fare` stores `flightId` (pointing to `Flight`).
- Consistency across boundaries is enforced through **OpenFeign REST APIs** and **RabbitMQ Events**.
