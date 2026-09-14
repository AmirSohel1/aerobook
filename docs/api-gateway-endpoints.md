# API Gateway Endpoint Catalog

All requests below use the gateway base URL:

```text
http://localhost:8083
```

For protected endpoints, send:

```http
Authorization: Bearer <ACCESS_TOKEN>
```

## Auth Service

| Method | Gateway endpoint           | Role                            |
| ------ | -------------------------- | ------------------------------- |
| POST   | `/api/auth/register`       | Public                          |
| POST   | `/api/auth/register-admin` | Public with `X-Admin-Setup-Key` |
| POST   | `/api/auth/login`          | Public                          |
| POST   | `/api/auth/refresh-token`  | Public                          |

`register-admin` creates or promotes an admin credential and returns a new admin token. It must include the configured `X-Admin-Setup-Key` header.

## User Service

| Method | Gateway endpoint     | Role                        |
| ------ | -------------------- | --------------------------- |
| GET    | `/api/v1/users`      | `ROLE_USER` or `ROLE_ADMIN` |
| GET    | `/api/v1/users/{id}` | `ROLE_USER` or `ROLE_ADMIN` |
| POST   | `/api/v1/users`      | `ROLE_ADMIN`                |
| PUT    | `/api/v1/users/{id}` | `ROLE_ADMIN`                |
| DELETE | `/api/v1/users/{id}` | `ROLE_ADMIN`                |

## Flight Service

| Method | Gateway endpoint                                                | Role                        |
| ------ | --------------------------------------------------------------- | --------------------------- |
| GET    | `/api/flights/search?source={source}&destination={destination}` | `ROLE_USER` or `ROLE_ADMIN` |
| GET    | `/api/flights/{id}`                                             | `ROLE_USER` or `ROLE_ADMIN` |
| POST   | `/api/admin/flights`                                            | `ROLE_ADMIN`                |
| GET    | `/api/admin/flights`                                            | `ROLE_ADMIN`                |
| GET    | `/api/admin/flights/{id}`                                       | `ROLE_ADMIN`                |
| PUT    | `/api/admin/flights/{id}`                                       | `ROLE_ADMIN`                |
| DELETE | `/api/admin/flights/{id}`                                       | `ROLE_ADMIN`                |
| POST   | `/api/admin/aircrafts`                                          | `ROLE_ADMIN`                |
| GET    | `/api/admin/aircrafts`                                          | `ROLE_ADMIN`                |
| GET    | `/api/admin/aircrafts/{id}`                                     | `ROLE_ADMIN`                |
| DELETE | `/api/admin/aircrafts/{id}`                                     | `ROLE_ADMIN`                |

## Fare Service

| Method | Gateway endpoint               | Role                        |
| ------ | ------------------------------ | --------------------------- |
| GET    | `/api/fares`                   | `ROLE_USER` or `ROLE_ADMIN` |
| GET    | `/api/fares/{id}`              | `ROLE_USER` or `ROLE_ADMIN` |
| GET    | `/api/fares/flight/{flightId}` | `ROLE_USER` or `ROLE_ADMIN` |
| POST   | `/api/fares`                   | `ROLE_ADMIN`                |
| PUT    | `/api/fares/{id}`              | `ROLE_ADMIN`                |
| DELETE | `/api/fares/{id}`              | `ROLE_ADMIN`                |

## Booking Service

| Method | Gateway endpoint              | Role                        |
| ------ | ----------------------------- | --------------------------- |
| POST   | `/api/bookings`               | `ROLE_USER` or `ROLE_ADMIN` |
| GET    | `/api/bookings/pnr/{pnr}`     | `ROLE_USER` or `ROLE_ADMIN` |
| GET    | `/api/bookings/user/{userId}` | `ROLE_USER` or `ROLE_ADMIN` |
| DELETE | `/api/bookings/{bookingId}`   | `ROLE_USER` or `ROLE_ADMIN` |

Booking creation validates the flight through Eureka/OpenFeign and publishes a `BOOKING_CREATED` RabbitMQ event.

## Check-in Service

| Method | Gateway endpoint                                                     | Role                        |
| ------ | -------------------------------------------------------------------- | --------------------------- |
| POST   | `/api/check-ins?bookingId={bookingId}&passengerName={passengerName}` | `ROLE_USER` or `ROLE_ADMIN` |
| GET    | `/api/check-ins/booking/{bookingId}`                                 | `ROLE_USER` or `ROLE_ADMIN` |

## Infrastructure

| Component     | Endpoint                | Purpose                   |
| ------------- | ----------------------- | ------------------------- |
| API Gateway   | `http://localhost:8083` | Single client entry point |
| Config Server | `http://localhost:8080` | Central configuration     |
| Eureka        | `http://localhost:8761` | Service discovery         |
