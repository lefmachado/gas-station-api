package br.com.lefmachado.gasstation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.lefmachado.gasstation.dto.FuelTypeRequest;
import br.com.lefmachado.gasstation.dto.FuelTypeResponse;
import br.com.lefmachado.gasstation.entity.FuelType;
import br.com.lefmachado.gasstation.repository.FuelPumpRepository;
import br.com.lefmachado.gasstation.repository.FuelTypeRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class FuelTypeServiceTest {

    @Mock
    private FuelTypeRepository fuelTypeRepository;

    @Mock
    private FuelPumpRepository fuelPumpRepository;

    @InjectMocks
    private FuelTypeService fuelTypeService;

    @Test
    void createShouldNormalizeNameAndPersistFuelType() {
        FuelTypeRequest request = new FuelTypeRequest("  Gasoline  ", new BigDecimal("5.899"));
        FuelType saved = FuelType.builder()
                .id(1L)
                .name("Gasoline")
                .literPrice(new BigDecimal("5.899"))
                .build();

        when(fuelTypeRepository.existsByNameIgnoreCase("Gasoline")).thenReturn(false);
        when(fuelTypeRepository.save(any(FuelType.class))).thenReturn(saved);

        FuelTypeResponse response = fuelTypeService.create(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Gasoline");
        assertThat(response.literPrice()).isEqualByComparingTo("5.899");
    }

    @Test
    void createShouldThrowConflictWhenNameAlreadyExists() {
        FuelTypeRequest request = new FuelTypeRequest("Gasoline", new BigDecimal("5.899"));
        when(fuelTypeRepository.existsByNameIgnoreCase("Gasoline")).thenReturn(true);

        assertThatThrownBy(() -> fuelTypeService.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(fuelTypeRepository, never()).save(any());
    }

    @Test
    void findAllShouldReturnMappedResponses() {
        FuelType gasoline = FuelType.builder().id(1L).name("Gasoline").literPrice(new BigDecimal("5.899")).build();
        FuelType ethanol = FuelType.builder().id(2L).name("Ethanol").literPrice(new BigDecimal("3.899")).build();
        when(fuelTypeRepository.findAll()).thenReturn(List.of(gasoline, ethanol));

        List<FuelTypeResponse> result = fuelTypeService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(FuelTypeResponse::name).containsExactly("Gasoline", "Ethanol");
    }

    @Test
    void findByIdShouldThrowNotFoundWhenMissing() {
        when(fuelTypeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fuelTypeService.findById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateShouldModifyFuelTypeWhenValid() {
        FuelType existing = FuelType.builder().id(1L).name("Gasoline").literPrice(new BigDecimal("5.899")).build();
        FuelTypeRequest request = new FuelTypeRequest("Gasoline Grid", new BigDecimal("6.299"));

        when(fuelTypeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(fuelTypeRepository.existsByNameIgnoreCaseAndIdNot("Gasoline Grid", 1L)).thenReturn(false);
        when(fuelTypeRepository.save(existing)).thenReturn(existing);

        FuelTypeResponse updated = fuelTypeService.update(1L, request);

        assertThat(updated.name()).isEqualTo("Gasoline Grid");
        assertThat(updated.literPrice()).isEqualByComparingTo("6.299");
    }

    @Test
    void updateShouldThrowConflictWhenNameUsedByAnotherFuelType() {
        FuelType existing = FuelType.builder().id(1L).name("Gasoline").literPrice(new BigDecimal("5.899")).build();
        FuelTypeRequest request = new FuelTypeRequest("Ethanol", new BigDecimal("4.100"));

        when(fuelTypeRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(fuelTypeRepository.existsByNameIgnoreCaseAndIdNot("Ethanol", 1L)).thenReturn(true);

        assertThatThrownBy(() -> fuelTypeService.update(1L, request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void deleteShouldThrowConflictWhenLinkedToFuelPumps() {
        when(fuelTypeRepository.existsById(1L)).thenReturn(true);
        when(fuelPumpRepository.existsByFuelTypeId(1L)).thenReturn(true);

        assertThatThrownBy(() -> fuelTypeService.delete(1L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(fuelTypeRepository, never()).deleteById(any());
    }

    @Test
    void deleteShouldRemoveFuelTypeWhenNotLinked() {
        when(fuelTypeRepository.existsById(1L)).thenReturn(true);
        when(fuelPumpRepository.existsByFuelTypeId(1L)).thenReturn(false);

        fuelTypeService.delete(1L);

        verify(fuelTypeRepository).deleteById(1L);
    }
}
