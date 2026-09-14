# API Gateway POST Request Guide

Base URL:

```text
http://localhost:8083
```

Protected requests require:

```http
Authorization: Bearer <ACCESS_TOKEN>
Content-Type: application/json
```

## Auth Service

### Register normal user

```http
POST /api/auth/register
```

Role: Public

```json
{
  "firstName": "Asha",
  "lastName": "Khan",
  "email": "asha.khan@example.com",
  "phoneNumber": "9876543210",
  "dateOfBirth": "1998-05-12",
  "nationality": "Indian",
  "password": "Asha@12345"
}
```

### Register or promote admin

```http
POST /api/auth/register-admin
X-Admin-Setup-Key: AerobookAdminSetup2026
```

Role: Setup key required

```json
{
  "firstName": "Aerobook",
  "lastName": "Admin",
  "email": "admin11@aerobook.com",
  "phoneNumber": "9876543211",
  "dateOfBirth": "1990-01-01",
  "nationality": "Indian",
  "password": "Admin@12345"
}
```

### Login

```http
POST /api/auth/login
```

Role: Public

```json
{
  "email": "asha.khan@example.com",
  "password": "Asha@12345"
}
```

### Refresh token

```http
POST /api/auth/refresh-token
```

Role: Public

```json
{
  "refreshToken": "<REFRESH_TOKEN_FROM_LOGIN_RESPONSE>"
}
```

## User Service

### Create user

```http
POST /api/v1/users
```

Role: `ROLE_ADMIN`

```json
{
  "firstName": "Ravi",
  "lastName": "Patel",
  "email": "ravi.patel@example.com",
  "phoneNumber": "9876543212",
  "dateOfBirth": "1995-09-20",
  "nationality": "Indian"
}
```

## Flight Service

### Create aircraft

```http
POST /api/admin/aircrafts
```

Role: `ROLE_ADMIN`

```json
{
  "aircraftCode": "A320-001",
  "aircraftName": "Airbus A320",
  "manufacturer": "Airbus",
  "capacity": 180
}
```

### Create flight

```http
POST /api/admin/flights
```

Role: `ROLE_ADMIN`

The `aircraftId` must already exist.

```json
{
  "flightNumber": "AI101",
  "airlineName": "Air India",
  "source": "Mumbai",
  "destination": "Delhi",
  "departureTime": "2026-10-01T09:30:00",
  "arrivalTime": "2026-10-01T11:45:00",
  "totalSeats": 180,
  "baseFare": 5500.0,
  "aircraftId": 1
}
```

## Fare Service

### Create fare

```http
POST /api/fares
```

Role: `ROLE_ADMIN`

The `flightId` must already exist.

```json
{
  "flightId": 1,
  "economyFare": 5500.0,
  "businessFare": 12500.0,
  "firstClassFare": 25000.0,
  "taxPercentage": 5.0,
  "discountPercentage": 0.0,
  "effectiveDate": "2026-10-01"
}
```

## Booking Service

### Create booking

```http
POST /api/bookings
```

Role: `ROLE_USER` or `ROLE_ADMIN`

The booking service validates `flightId` through Eureka/OpenFeign and calculates the fare from the flight base fare.

```json
{
  "userId": 1,
  "flightId": 1,
  "passengers": [
    {
      "firstName": "Asha",
      "lastName": "Khan",
      "age": 28,
      "gender": "F"
    }
  ]
}
```

## Check-in Service

### Check in passenger

```http
POST /api/check-ins?bookingId=1&passengerName=Asha%20Khan
```

Role: `ROLE_USER` or `ROLE_ADMIN`

This endpoint uses query parameters and does not accept a JSON body.

## PUT Request Example

### Update fare

```http
PUT /api/fares/1
```

Role: `ROLE_ADMIN`

```json
{
  "flightId": 1,
  "economyFare": 5750.0,
  "businessFare": 13000.0,
  "firstClassFare": 26000.0,
  "taxPercentage": 5.0,
  "discountPercentage": 10.0,
  "effectiveDate": "2026-10-01"
}
```

## RabbitMQ Usage

RabbitMQ is used for asynchronous booking events:

1. Booking Service publishes `BOOKING_CREATED:<pnr>` to the durable queue `booking-created`.
2. Flight Service declares and consumes the `booking-created` queue.
3. Booking Service uses `RabbitTemplate`.
4. Flight Service uses `@RabbitListener`.
5. RabbitMQ defaults are `localhost:5672`, username `guest`, password `guest`.

RabbitMQ is currently used for booking messaging. Application logs still use normal Spring logging; a separate centralized logging consumer has not been implemented yet.
