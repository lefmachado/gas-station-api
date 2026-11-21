package br.com.lefmachado.gasstation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.lefmachado.gasstation.dto.FuelPumpRequest;
import br.com.lefmachado.gasstation.dto.FuelPumpResponse;
import br.com.lefmachado.gasstation.entity.FuelPump;
import br.com.lefmachado.gasstation.entity.FuelType;
import br.com.lefmachado.gasstation.repository.FuelPumpRepository;
import br.com.lefmachado.gasstation.repository.FuelTypeRepository;
import br.com.lefmachado.gasstation.repository.RefuelingRepository;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class FuelPumpServiceTest {

    @Mock
    private FuelPumpRepository fuelPumpRepository;

    @Mock
    private FuelTypeRepository fuelTypeRepository;

    @Mock
    private RefuelingRepository refuelingRepository;

    @InjectMocks
    private FuelPumpService fuelPumpService;

    @Test
    void createShouldPersistPumpLinkedToFuelType() {
        FuelType gasoline = FuelType.builder().id(1L).name("Gasoline").literPrice(new BigDecimal("5.899")).build();
        FuelPump saved = FuelPump.builder().id(10L).name("Pump 1").fuelType(gasoline).build();

        when(fuelTypeRepository.findById(1L)).thenReturn(Optional.of(gasoline));
        when(fuelPumpRepository.save(any(FuelPump.class))).thenReturn(saved);

        FuelPumpResponse response = fuelPumpService.create(new FuelPumpRequest("  Pump 1  ", 1L));

        assertThat(response.id()).isEqualTo(10L);
        assertThat(response.name()).isEqualTo("Pump 1");
        assertThat(response.fuelType().id()).isEqualTo(1L);
    }

    @Test
    void createShouldThrowNotFoundWhenFuelTypeDoesNotExist() {
        when(fuelTypeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fuelPumpService.create(new FuelPumpRequest("Pump 1", 99L)))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateShouldChangePumpNameAndFuelType() {
        FuelType gasoline = FuelType.builder().id(1L).name("Gasoline").literPrice(new BigDecimal("5.899")).build();
        FuelType ethanol = FuelType.builder().id(2L).name("Ethanol").literPrice(new BigDecimal("3.899")).build();
        FuelPump existing = FuelPump.builder().id(10L).name("Pump 1").fuelType(gasoline).build();

        when(fuelPumpRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(fuelTypeRepository.findById(2L)).thenReturn(Optional.of(ethanol));
        when(fuelPumpRepository.save(existing)).thenReturn(existing);

        FuelPumpResponse updated = fuelPumpService.update(10L, new FuelPumpRequest("Pump 1B", 2L));

        assertThat(updated.name()).isEqualTo("Pump 1B");
        assertThat(updated.fuelType().id()).isEqualTo(2L);
    }

    @Test
    void deleteShouldThrowConflictWhenPumpHasRefuelings() {
        when(fuelPumpRepository.existsById(10L)).thenReturn(true);
        when(refuelingRepository.existsByFuelPumpId(10L)).thenReturn(true);

        assertThatThrownBy(() -> fuelPumpService.delete(10L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(fuelPumpRepository, never()).deleteById(any());
    }

    @Test
    void deleteShouldRemovePumpWhenNoRefuelingsExist() {
        when(fuelPumpRepository.existsById(10L)).thenReturn(true);
        when(refuelingRepository.existsByFuelPumpId(10L)).thenReturn(false);

        fuelPumpService.delete(10L);

        verify(fuelPumpRepository).deleteById(10L);
    }
}
