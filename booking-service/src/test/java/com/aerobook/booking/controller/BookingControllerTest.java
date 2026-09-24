package com.aerobook.booking.controller;

import com.aerobook.booking.dto.BookingRequest;
import com.aerobook.booking.dto.BookingResponse;
import com.aerobook.booking.dto.PassengerRequest;
import com.aerobook.booking.exception.GlobalExceptionHandler;
import com.aerobook.booking.exception.ResourceNotFoundException;
import com.aerobook.booking.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Booking Controller Unit Tests")
class BookingControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookingService bookingService;

    @InjectMocks
    private BookingController bookingController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private BookingResponse sampleBookingResponse;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        sampleBookingResponse = new BookingResponse();
        sampleBookingResponse.setBookingId(1L);
        sampleBookingResponse.setPnr("AB1001");
        sampleBookingResponse.setUserId(1L);
        sampleBookingResponse.setFlightId(101L);
        sampleBookingResponse.setStatus("BOOKED");
        sampleBookingResponse.setTotalFare(BigDecimal.valueOf(4500));
        sampleBookingResponse.setBookingDate(LocalDateTime.now());
        sampleBookingResponse.setFlightNumber("AI101");
        sampleBookingResponse.setAirlineName("Air India");
    }

    @Test
    @DisplayName("GET /api/bookings/health should return UP status")
    void checkHealth_ReturnsOk() throws Exception {
        mockMvc.perform(get("/api/bookings/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"))
                .andExpect(jsonPath("$.service").value("booking-service"))
                .andExpect(jsonPath("$.port").value(8088));
    }

    @Test
    @DisplayName("POST /api/bookings with valid payload should create booking")
    void createBooking_ValidPayload_ReturnsOk() throws Exception {
        PassengerRequest passenger = new PassengerRequest();
        passenger.setFirstName("Asha");
        passenger.setLastName("Khan");
        passenger.setAge(29);
        passenger.setGender("F");

        BookingRequest request = new BookingRequest();
        request.setUserId(1L);
        request.setFlightId(101L);
        request.setPassengers(List.of(passenger));

        when(bookingService.createBooking(any(BookingRequest.class))).thenReturn(sampleBookingResponse);

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pnr").value("AB1001"))
                .andExpect(jsonPath("$.status").value("BOOKED"))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.flightId").value(101));
    }

    @Test
    @DisplayName("POST /api/bookings with invalid payload (missing passengers) should return 400")
    void createBooking_InvalidPayload_ReturnsBadRequest() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setUserId(1L);
        request.setFlightId(101L);

        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation Failed"));
    }

    @Test
    @DisplayName("GET /api/bookings/{id} should return booking when found")
    void getBookingById_ReturnsOk() throws Exception {
        when(bookingService.getBookingById(1L)).thenReturn(sampleBookingResponse);

        mockMvc.perform(get("/api/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookingId").value(1))
                .andExpect(jsonPath("$.pnr").value("AB1001"));
    }

    @Test
    @DisplayName("GET /api/bookings/{id} should return 404 when booking not found")
    void getBookingById_NotFound_Returns404() throws Exception {
        when(bookingService.getBookingById(999L))
                .thenThrow(new ResourceNotFoundException("Booking not found with ID: 999"));

        mockMvc.perform(get("/api/bookings/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message", containsString("Booking not found with ID: 999")));
    }

    @Test
    @DisplayName("GET /api/bookings/pnr/{pnr} should return booking details")
    void getBookingByPnr_ReturnsOk() throws Exception {
        when(bookingService.getBookingByPnr("AB1001")).thenReturn(sampleBookingResponse);

        mockMvc.perform(get("/api/bookings/pnr/AB1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pnr").value("AB1001"));
    }

    @Test
    @DisplayName("GET /api/bookings/user/{userId} should return user bookings")
    void getBookingsByUser_ReturnsOk() throws Exception {
        when(bookingService.getBookingsByUserId(1L)).thenReturn(List.of(sampleBookingResponse));

        mockMvc.perform(get("/api/bookings/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pnr").value("AB1001"));
    }

    @Test
    @DisplayName("GET /api/bookings with ROLE_ADMIN header should allow listing all bookings")
    void getAllBookings_AdminAllowed_ReturnsOk() throws Exception {
        when(bookingService.getAllBookings()).thenReturn(List.of(sampleBookingResponse));

        mockMvc.perform(get("/api/bookings")
                .header("X-User-Role", "ROLE_ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pnr").value("AB1001"));
    }

    @Test
    @DisplayName("GET /api/bookings with ROLE_STAFF header should allow listing all bookings")
    void getAllBookings_StaffAllowed_ReturnsOk() throws Exception {
        when(bookingService.getAllBookings()).thenReturn(List.of(sampleBookingResponse));

        mockMvc.perform(get("/api/bookings")
                .header("X-User-Role", "ROLE_STAFF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pnr").value("AB1001"));
    }

    @Test
    @DisplayName("GET /api/bookings with ROLE_USER header should return 403 Forbidden")
    void getAllBookings_UserForbidden_Returns403() throws Exception {
        mockMvc.perform(get("/api/bookings")
                .header("X-User-Role", "ROLE_USER"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message", containsString("ROLE_ADMIN privileges")));
    }

    @Test
    @DisplayName("GET /api/bookings/flight/{flightId} with ROLE_STAFF should return flight passenger manifest")
    void getBookingsByFlight_StaffAllowed_ReturnsManifest() throws Exception {
        when(bookingService.getBookingsByFlightId(101L)).thenReturn(List.of(sampleBookingResponse));

        mockMvc.perform(get("/api/bookings/flight/101")
                .header("X-User-Role", "ROLE_STAFF"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].pnr").value("AB1001"));
    }

    @Test
    @DisplayName("GET /api/bookings/flight/{flightId} with ROLE_USER should return 403 Forbidden")
    void getBookingsByFlight_UserForbidden_Returns403() throws Exception {
        mockMvc.perform(get("/api/bookings/flight/101")
                .header("X-User-Role", "ROLE_USER"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403))
                .andExpect(jsonPath("$.error").value("Forbidden"))
                .andExpect(jsonPath("$.message", containsString("ROLE_ADMIN or ROLE_STAFF")));
    }

    @Test
    @DisplayName("DELETE /api/bookings/{id} should return success message")
    void cancelBooking_ReturnsOk() throws Exception {
        when(bookingService.cancelBooking(1L)).thenReturn("Booking Cancelled Successfully");

        mockMvc.perform(delete("/api/bookings/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Booking Cancelled Successfully"));
    }
}
