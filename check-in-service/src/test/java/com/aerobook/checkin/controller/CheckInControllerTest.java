package com.aerobook.checkin.controller;

import com.aerobook.checkin.dto.CheckInRequest;
import com.aerobook.checkin.entity.CheckIn;
import com.aerobook.checkin.service.CheckInService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckInControllerTest {

    @Mock
    private CheckInService checkInService;

    @InjectMocks
    private CheckInController checkInController;

    private CheckIn testCheckIn;
    private CheckInRequest checkInRequest;

    @BeforeEach
    void setUp() {
        testCheckIn = new CheckIn();
        testCheckIn.setId(1L);
        testCheckIn.setBookingId(10L);
        testCheckIn.setPassengerName("Asha Khan");
        testCheckIn.setSeatNumber("12A");
        testCheckIn.setBoardingPassNumber("BP-10-12A");
        testCheckIn.setStatus("CHECKED_IN");
        testCheckIn.setCheckedInAt(LocalDateTime.now());

        checkInRequest = new CheckInRequest();
        checkInRequest.setBookingId(10L);
        checkInRequest.setPassengerName("Asha Khan");
        checkInRequest.setSeatNumber("12a");
    }

    @Test
    void checkHealth_ReturnsOkWithStatusUp() {
        ResponseEntity<Map<String, Object>> response = checkInController.checkHealth();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().get("status")).isEqualTo("UP");
        assertThat(response.getBody().get("service")).isEqualTo("check-in-service");
        assertThat(response.getBody().get("port")).isEqualTo(8090);
    }

    @Test
    void checkIn_Success_AssignsSeatAndGeneratesBoardingPass() {
        when(checkInService.performCheckIn(checkInRequest)).thenReturn(testCheckIn);

        ResponseEntity<CheckIn> response = checkInController.checkIn(checkInRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        CheckIn result = response.getBody();
        assertThat(result).isNotNull();
        assertThat(result.getBookingId()).isEqualTo(10L);
        assertThat(result.getPassengerName()).isEqualTo("Asha Khan");
        assertThat(result.getSeatNumber()).isEqualTo("12A");
        assertThat(result.getBoardingPassNumber()).isEqualTo("BP-10-12A");
        assertThat(result.getStatus()).isEqualTo("CHECKED_IN");
        verify(checkInService).performCheckIn(checkInRequest);
    }

    @Test
    void getByBooking_Found_ReturnsCheckInRecord() {
        when(checkInService.getCheckInByBookingId(10L)).thenReturn(testCheckIn);

        ResponseEntity<CheckIn> response = checkInController.getByBooking(10L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getBoardingPassNumber()).isEqualTo("BP-10-12A");
    }

    @Test
    void getByBooking_NotFound_ThrowsResponseStatusException() {
        when(checkInService.getCheckInByBookingId(99L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Check-in not found for booking ID: 99"));

        assertThatThrownBy(() -> checkInController.getByBooking(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Check-in not found for booking ID: 99");
    }

    @Test
    void getById_Found_ReturnsCheckInRecord() {
        when(checkInService.getCheckInById(1L)).thenReturn(testCheckIn);

        ResponseEntity<CheckIn> response = checkInController.getById(1L);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getId()).isEqualTo(1L);
    }

    @Test
    void getAllCheckIns_AdminAllowed_ReturnsList() {
        when(checkInService.getAllCheckIns()).thenReturn(List.of(testCheckIn));

        ResponseEntity<?> response = checkInController.getAllCheckIns("ROLE_ADMIN");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<?> checkIns = (List<?>) response.getBody();
        assertThat(checkIns).hasSize(1);
    }

    @Test
    void getAllCheckIns_UserRole_ReturnsForbidden() {
        ResponseEntity<?> response = checkInController.getAllCheckIns("ROLE_USER");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verify(checkInService, never()).getAllCheckIns();
    }

    @Test
    void getAllCheckIns_StaffAllowed_ReturnsList() {
        when(checkInService.getAllCheckIns()).thenReturn(List.of(testCheckIn));

        ResponseEntity<?> response = checkInController.getAllCheckIns("ROLE_STAFF");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<?> checkIns = (List<?>) response.getBody();
        assertThat(checkIns).hasSize(1);
    }

    @Test
    void verifyBoardingPass_Success_MarksBoarded() {
        testCheckIn.setStatus("BOARDED");
        when(checkInService.verifyBoardingPass("BP-10-12A")).thenReturn(testCheckIn);

        ResponseEntity<?> response = checkInController.verifyBoardingPass("BP-10-12A", "ROLE_STAFF");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        CheckIn result = (CheckIn) response.getBody();
        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("BOARDED");
        verify(checkInService).verifyBoardingPass("BP-10-12A");
    }
}
