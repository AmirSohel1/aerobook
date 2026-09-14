package com.aerobook.booking.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ============================================================================
 * Flight Service OpenFeign Declarative Client
 * ============================================================================
 *
 * Discovers and queries downstream {@code flight-service} instances via Eureka
 * service discovery. Used by Booking Service to validate flight schedules,
 * verify real-time seat availability, and retrieve ticket base pricing.
 *
 * @author Aerobook Platform Engineering
 * @version 1.0.0
 */
@FeignClient(name = "flight-service")
public interface FlightServiceClient {

    /**
     * Resolves flight schedule coordinates and seat availability by primary
     * flight ID.
     *
     * @param id flight database identifier
     * @return {@link FlightResponse} containing flight operational status and
     * remaining seats
     */
    @GetMapping("/api/flights/{id}")
    FlightResponse getFlightById(@PathVariable("id") Long id);

    /**
     * Internal DTO projection of flight schedule attributes returned by Flight
     * Service.
     */
    class FlightResponse {

        private Long id;
        private String flightNumber;
        private String airlineName;
        private String source;
        private String destination;
        private LocalDateTime departureTime;
        private LocalDateTime arrivalTime;
        private Integer totalSeats;
        private Integer availableSeats;
        private BigDecimal baseFare;
        private String status;

        public FlightResponse() {
        }

        public FlightResponse(Long id, String flightNumber, String airlineName, String source,
                String destination, LocalDateTime departureTime, LocalDateTime arrivalTime,
                Integer totalSeats, Integer availableSeats, BigDecimal baseFare, String status) {
            this.id = id;
            this.flightNumber = flightNumber;
            this.airlineName = airlineName;
            this.source = source;
            this.destination = destination;
            this.departureTime = departureTime;
            this.arrivalTime = arrivalTime;
            this.totalSeats = totalSeats;
            this.availableSeats = availableSeats;
            this.baseFare = baseFare;
            this.status = status;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getFlightId() {
            return id;
        }

        public void setFlightId(Long flightId) {
            this.id = flightId;
        }

        public String getFlightNumber() {
            return flightNumber;
        }

        public void setFlightNumber(String flightNumber) {
            this.flightNumber = flightNumber;
        }

        public String getAirlineName() {
            return airlineName;
        }

        public void setAirlineName(String airlineName) {
            this.airlineName = airlineName;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getDestination() {
            return destination;
        }

        public void setDestination(String destination) {
            this.destination = destination;
        }

        public LocalDateTime getDepartureTime() {
            return departureTime;
        }

        public void setDepartureTime(LocalDateTime departureTime) {
            this.departureTime = departureTime;
        }

        public LocalDateTime getArrivalTime() {
            return arrivalTime;
        }

        public void setArrivalTime(LocalDateTime arrivalTime) {
            this.arrivalTime = arrivalTime;
        }

        public Integer getTotalSeats() {
            return totalSeats;
        }

        public void setTotalSeats(Integer totalSeats) {
            this.totalSeats = totalSeats;
        }

        public Integer getAvailableSeats() {
            return availableSeats;
        }

        public void setAvailableSeats(Integer availableSeats) {
            this.availableSeats = availableSeats;
        }

        public BigDecimal getBaseFare() {
            return baseFare;
        }

        public void setBaseFare(BigDecimal baseFare) {
            this.baseFare = baseFare;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}
