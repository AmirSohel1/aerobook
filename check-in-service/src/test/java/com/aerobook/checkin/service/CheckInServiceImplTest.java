package com.aerobook.checkin.service;

import com.aerobook.checkin.dto.CheckInRequest;
import com.aerobook.checkin.entity.CheckIn;
import com.aerobook.checkin.repository.CheckInRepository;
import com.aerobook.checkin.service.impl.CheckInServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CheckInServiceImplTest {

    @Mock
    private CheckInRepository checkInRepository;

    @InjectMocks
    private CheckInServiceImpl checkInService;

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
        checkInRequest.setSeatNumber("14b");
    }

    @Test
    void performCheckIn_NewBooking_AssignsSeatAndBoardingPass() {
        when(checkInRepository.findByBookingId(10L)).thenReturn(Optional.empty());
        when(checkInRepository.save(any(CheckIn.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CheckIn result = checkInService.performCheckIn(checkInRequest);

        assertThat(result).isNotNull();
        assertThat(result.getBookingId()).isEqualTo(10L);
        assertThat(result.getPassengerName()).isEqualTo("Asha Khan");
        assertThat(result.getSeatNumber()).isEqualTo("14B");
        assertThat(result.getBoardingPassNumber()).isEqualTo("BP-10-14B");
        assertThat(result.getStatus()).isEqualTo("CHECKED_IN");
        verify(checkInRepository).save(any(CheckIn.class));
    }

    @Test
    void getCheckInByBookingId_Found_ReturnsRecord() {
        when(checkInRepository.findByBookingId(10L)).thenReturn(Optional.of(testCheckIn));

        CheckIn result = checkInService.getCheckInByBookingId(10L);

        assertThat(result).isNotNull();
        assertThat(result.getBoardingPassNumber()).isEqualTo("BP-10-12A");
    }

    @Test
    void getCheckInByBookingId_NotFound_ThrowsResponseStatusException() {
        when(checkInRepository.findByBookingId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> checkInService.getCheckInByBookingId(99L))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Check-in not found for booking ID: 99");
    }

    @Test
    void getCheckInById_Found_ReturnsRecord() {
        when(checkInRepository.findById(1L)).thenReturn(Optional.of(testCheckIn));

        CheckIn result = checkInService.getCheckInById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void getAllCheckIns_ReturnsList() {
        when(checkInRepository.findAll()).thenReturn(List.of(testCheckIn));

        List<CheckIn> result = checkInService.getAllCheckIns();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBoardingPassNumber()).isEqualTo("BP-10-12A");
    }

    @Test
    void verifyBoardingPass_Found_UpdatesToBoarded() {
        when(checkInRepository.findByBoardingPassNumber("BP-10-12A")).thenReturn(Optional.of(testCheckIn));
        when(checkInRepository.save(any(CheckIn.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CheckIn result = checkInService.verifyBoardingPass("BP-10-12A");

        assertThat(result).isNotNull();
        assertThat(result.getStatus()).isEqualTo("BOARDED");
        verify(checkInRepository).save(testCheckIn);
    }

    @Test
    void verifyBoardingPass_NotFound_ThrowsException() {
        when(checkInRepository.findByBoardingPassNumber("BP-INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> checkInService.verifyBoardingPass("BP-INVALID"))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("Boarding pass not found: BP-INVALID");
    }
}
