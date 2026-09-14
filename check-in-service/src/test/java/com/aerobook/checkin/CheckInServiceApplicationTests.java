package com.aerobook.checkin;

import com.aerobook.checkin.entity.CheckIn;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class CheckInServiceApplicationTests {

    @Test
    void checkInEntityInstantiatesCorrectly() {
        CheckIn checkIn = new CheckIn();
        checkIn.setId(1L);
        checkIn.setBookingId(10L);
        checkIn.setPassengerName("Asha Khan");
        checkIn.setSeatNumber("14B");
        checkIn.setBoardingPassNumber("BP-10-14B");
        checkIn.setStatus("CHECKED_IN");
        checkIn.setCheckedInAt(LocalDateTime.now());

        assertThat(checkIn.getId()).isEqualTo(1L);
        assertThat(checkIn.getBookingId()).isEqualTo(10L);
        assertThat(checkIn.getBoardingPassNumber()).isEqualTo("BP-10-14B");
        assertThat(checkIn.getStatus()).isEqualTo("CHECKED_IN");
    }

}
