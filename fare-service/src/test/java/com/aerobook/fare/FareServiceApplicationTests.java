package com.aerobook.fare;

import com.aerobook.fare.entity.Fare;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class FareServiceApplicationTests {

    @Test
    void fareEntityInstantiatesCorrectly() {
        Fare fare = new Fare();
        fare.setId(1L);
        fare.setFlightId(101L);
        fare.setEconomyFare(BigDecimal.valueOf(4500));
        fare.setBusinessFare(BigDecimal.valueOf(8500));
        fare.setFirstClassFare(BigDecimal.valueOf(14000));
        fare.setTaxPercentage(BigDecimal.valueOf(18.0));
        fare.setDiscountPercentage(BigDecimal.valueOf(5.0));
        fare.setEffectiveDate(LocalDate.now());
        fare.setActive(true);

        assertThat(fare.getId()).isEqualTo(1L);
        assertThat(fare.getFlightId()).isEqualTo(101L);
        assertThat(fare.getEconomyFare()).isEqualByComparingTo("4500");
        assertThat(fare.getActive()).isTrue();
    }

}
