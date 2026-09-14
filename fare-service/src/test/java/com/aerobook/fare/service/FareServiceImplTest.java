package com.aerobook.fare.service;

import com.aerobook.fare.dto.request.FareRequest;
import com.aerobook.fare.dto.response.FareResponse;
import com.aerobook.fare.entity.Fare;
import com.aerobook.fare.exception.ResourceNotFoundException;
import com.aerobook.fare.repository.FareRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FareServiceImplTest {

    @Mock
    private FareRepository fareRepository;

    @InjectMocks
    private FareServiceImpl fareService;

    private Fare testFare;
    private FareRequest fareRequest;

    @BeforeEach
    void setUp() {
        testFare = new Fare();
        testFare.setId(1L);
        testFare.setFlightId(100L);
        testFare.setEconomyFare(BigDecimal.valueOf(4500.00));
        testFare.setBusinessFare(BigDecimal.valueOf(8500.00));
        testFare.setFirstClassFare(BigDecimal.valueOf(14000.00));
        testFare.setTaxPercentage(BigDecimal.valueOf(18.0));
        testFare.setDiscountPercentage(BigDecimal.valueOf(5.0));
        testFare.setEffectiveDate(LocalDate.now());
        testFare.setActive(true);

        fareRequest = new FareRequest();
        fareRequest.setFlightId(100L);
        fareRequest.setEconomyFare(BigDecimal.valueOf(4500.00));
        fareRequest.setBusinessFare(BigDecimal.valueOf(8500.00));
        fareRequest.setFirstClassFare(BigDecimal.valueOf(14000.00));
        fareRequest.setTaxPercentage(BigDecimal.valueOf(18.0));
        fareRequest.setDiscountPercentage(BigDecimal.valueOf(5.0));
        fareRequest.setEffectiveDate(LocalDate.now());
    }

    @Test
    void createFare_Success_SavesAndReturnsFareResponse() {
        when(fareRepository.save(any(Fare.class))).thenReturn(testFare);

        FareResponse response = fareService.createFare(fareRequest);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFlightId()).isEqualTo(100L);
        assertThat(response.getEconomyFare()).isEqualByComparingTo("4500.00");
        assertThat(response.getBusinessFare()).isEqualByComparingTo("8500.00");
        assertThat(response.getFirstClassFare()).isEqualByComparingTo("14000.00");
        assertThat(response.getActive()).isTrue();
        verify(fareRepository).save(any(Fare.class));
    }

    @Test
    void createFare_NegativeEconomyFare_ThrowsIllegalArgumentException() {
        fareRequest.setEconomyFare(BigDecimal.valueOf(-100));

        assertThatThrownBy(() -> fareService.createFare(fareRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Economy fare cannot be negative");

        verify(fareRepository, never()).save(any(Fare.class));
    }

    @Test
    void createFare_TaxOver100_ThrowsIllegalArgumentException() {
        fareRequest.setTaxPercentage(BigDecimal.valueOf(150.0));

        assertThatThrownBy(() -> fareService.createFare(fareRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Tax percentage must be between 0.0% and 100.0%");

        verify(fareRepository, never()).save(any(Fare.class));
    }

    @Test
    void getFareById_Success_ReturnsFareResponse() {
        when(fareRepository.findById(1L)).thenReturn(Optional.of(testFare));

        FareResponse response = fareService.getFareById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFlightId()).isEqualTo(100L);
    }

    @Test
    void getFareById_NotFound_ThrowsResourceNotFoundException() {
        when(fareRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fareService.getFareById(999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Fare not found with id: 999");
    }

    @Test
    void getAllFares_ReturnsListOfFares() {
        when(fareRepository.findAll()).thenReturn(List.of(testFare));

        List<FareResponse> fares = fareService.getAllFares();

        assertThat(fares).hasSize(1);
        assertThat(fares.get(0).getId()).isEqualTo(1L);
    }

    @Test
    void deleteFare_Success_DeletesFare() {
        when(fareRepository.findById(1L)).thenReturn(Optional.of(testFare));
        doNothing().when(fareRepository).delete(testFare);

        fareService.deleteFare(1L);

        verify(fareRepository).delete(testFare);
    }
}
