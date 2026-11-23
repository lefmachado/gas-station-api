package br.com.lefmachado.gasstation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.lefmachado.gasstation.dto.RefuelingRequest;
import br.com.lefmachado.gasstation.dto.RefuelingResponse;
import br.com.lefmachado.gasstation.entity.FuelPump;
import br.com.lefmachado.gasstation.entity.FuelType;
import br.com.lefmachado.gasstation.entity.Refueling;
import br.com.lefmachado.gasstation.repository.FuelPumpRepository;
import br.com.lefmachado.gasstation.repository.RefuelingRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class RefuelingServiceTest {

    @Mock
    private RefuelingRepository refuelingRepository;

    @Mock
    private FuelPumpRepository fuelPumpRepository;

    @InjectMocks
    private RefuelingService refuelingService;

    @Test
    void createShouldCalculateLitersFromTotalAmountAndSnapshotPrice() {
        FuelType gasoline = FuelType.builder().id(1L).name("Gasoline").literPrice(new BigDecimal("5.899")).build();
        FuelPump pump = FuelPump.builder().id(1L).name("Pump 1").fuelType(gasoline).build();
        LocalDateTime now = LocalDateTime.of(2026, 11, 20, 14, 30);

        when(fuelPumpRepository.findById(1L)).thenReturn(Optional.of(pump));
        when(refuelingRepository.save(any(Refueling.class))).thenAnswer(invocation -> {
            Refueling entity = invocation.getArgument(0);
            entity.setId(100L);
            return entity;
        });

        RefuelingRequest request = new RefuelingRequest(1L, now, null, new BigDecimal("100.00"));
        RefuelingResponse response = refuelingService.create(request);

        assertThat(response.id()).isEqualTo(100L);
        assertThat(response.liters()).isEqualByComparingTo("16.952");
        assertThat(response.totalAmount()).isEqualByComparingTo("100.00");
        assertThat(response.appliedLiterPrice()).isEqualByComparingTo("5.899");
    }

    @Test
    void createShouldCalculateTotalAmountFromLitersAndSnapshotPrice() {
        FuelType gasoline = FuelType.builder().id(1L).name("Gasoline").literPrice(new BigDecimal("5.899")).build();
        FuelPump pump = FuelPump.builder().id(1L).name("Pump 1").fuelType(gasoline).build();
        LocalDateTime now = LocalDateTime.of(2026, 11, 20, 14, 30);

        when(fuelPumpRepository.findById(1L)).thenReturn(Optional.of(pump));
        when(refuelingRepository.save(any(Refueling.class))).thenAnswer(invocation -> {
            Refueling entity = invocation.getArgument(0);
            entity.setId(101L);
            return entity;
        });

        RefuelingRequest request = new RefuelingRequest(1L, now, new BigDecimal("20.000"), null);
        RefuelingResponse response = refuelingService.create(request);

        assertThat(response.liters()).isEqualByComparingTo("20.000");
        assertThat(response.totalAmount()).isEqualByComparingTo("117.98");
        assertThat(response.appliedLiterPrice()).isEqualByComparingTo("5.899");
    }

    @Test
    void createShouldThrowBadRequestWhenBothLitersAndTotalAmountAreProvided() {
        FuelType gasoline = FuelType.builder().id(1L).name("Gasoline").literPrice(new BigDecimal("5.899")).build();
        FuelPump pump = FuelPump.builder().id(1L).name("Pump 1").fuelType(gasoline).build();

        when(fuelPumpRepository.findById(1L)).thenReturn(Optional.of(pump));

        RefuelingRequest request = new RefuelingRequest(
                1L, LocalDateTime.now(), new BigDecimal("10.000"), new BigDecimal("50.00"));

        assertThatThrownBy(() -> refuelingService.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void createShouldThrowBadRequestWhenNeitherLitersNorTotalAmountIsProvided() {
        FuelType gasoline = FuelType.builder().id(1L).name("Gasoline").literPrice(new BigDecimal("5.899")).build();
        FuelPump pump = FuelPump.builder().id(1L).name("Pump 1").fuelType(gasoline).build();

        when(fuelPumpRepository.findById(1L)).thenReturn(Optional.of(pump));

        RefuelingRequest request = new RefuelingRequest(1L, LocalDateTime.now(), null, null);

        assertThatThrownBy(() -> refuelingService.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void updateShouldPreserveHistoricalAppliedLiterPriceWhenFuelTypeIsUnchanged() {
        FuelType gasolineUpdated = FuelType.builder().id(1L).name("Gasoline").literPrice(new BigDecimal("6.500")).build();
        FuelPump pump = FuelPump.builder().id(1L).name("Pump 1").fuelType(gasolineUpdated).build();
        Refueling existing = Refueling.builder()
                .id(100L)
                .fuelPump(pump)
                .refueledAt(LocalDateTime.of(2026, 11, 20, 10, 0))
                .liters(new BigDecimal("16.952"))
                .totalAmount(new BigDecimal("100.00"))
                .appliedLiterPrice(new BigDecimal("5.899"))
                .build();

        when(refuelingRepository.findById(100L)).thenReturn(Optional.of(existing));
        when(fuelPumpRepository.findById(1L)).thenReturn(Optional.of(pump));
        when(refuelingRepository.save(existing)).thenReturn(existing);

        RefuelingRequest request = new RefuelingRequest(1L, null, new BigDecimal("20.000"), null);
        RefuelingResponse response = refuelingService.update(100L, request);

        assertThat(response.appliedLiterPrice()).isEqualByComparingTo("5.899");
        assertThat(response.totalAmount()).isEqualByComparingTo("117.98");
    }

    @Test
    void deleteShouldThrowNotFoundWhenMissing() {
        when(refuelingRepository.existsById(999L)).thenReturn(false);

        assertThatThrownBy(() -> refuelingService.delete(999L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void deleteShouldRemoveExistingRefueling() {
        when(refuelingRepository.existsById(100L)).thenReturn(true);

        refuelingService.delete(100L);

        verify(refuelingRepository).deleteById(100L);
    }
}
