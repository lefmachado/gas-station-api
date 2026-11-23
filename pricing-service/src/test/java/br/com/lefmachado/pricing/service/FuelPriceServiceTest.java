package br.com.lefmachado.pricing.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.lefmachado.pricing.dto.FuelPriceRequest;
import br.com.lefmachado.pricing.dto.FuelPriceResponse;
import br.com.lefmachado.pricing.entity.FuelPrice;
import br.com.lefmachado.pricing.repository.FuelPriceRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
class FuelPriceServiceTest {

    @Mock
    private FuelPriceRepository fuelPriceRepository;

    @InjectMocks
    private FuelPriceService fuelPriceService;

    @Test
    void createShouldNormalizeNameAndPersistFuelPrice() {
        FuelPriceRequest request = new FuelPriceRequest("  Gasoline  ", new BigDecimal("5.899"));
        FuelPrice saved = FuelPrice.builder()
                .id(1L)
                .name("Gasoline")
                .literPrice(new BigDecimal("5.899"))
                .updatedAt(OffsetDateTime.now(ZoneOffset.UTC))
                .build();

        when(fuelPriceRepository.existsByNameIgnoreCase("Gasoline")).thenReturn(false);
        when(fuelPriceRepository.save(any(FuelPrice.class))).thenReturn(saved);

        FuelPriceResponse response = fuelPriceService.create(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Gasoline");
        assertThat(response.literPrice()).isEqualByComparingTo("5.899");
    }

    @Test
    void createShouldThrowConflictWhenDuplicateNameExists() {
        FuelPriceRequest request = new FuelPriceRequest("Gasoline", new BigDecimal("5.899"));
        when(fuelPriceRepository.existsByNameIgnoreCase("Gasoline")).thenReturn(true);

        assertThatThrownBy(() -> fuelPriceService.create(request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(fuelPriceRepository, never()).save(any());
    }

    @Test
    void findAllShouldReturnAllFuelPrices() {
        FuelPrice gasoline = FuelPrice.builder().id(1L).name("Gasoline").literPrice(new BigDecimal("5.899")).build();
        FuelPrice ethanol = FuelPrice.builder().id(2L).name("Ethanol").literPrice(new BigDecimal("3.899")).build();
        when(fuelPriceRepository.findAll()).thenReturn(List.of(gasoline, ethanol));

        List<FuelPriceResponse> result = fuelPriceService.findAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(FuelPriceResponse::name).containsExactly("Gasoline", "Ethanol");
    }

    @Test
    void findByIdShouldThrowNotFoundWhenMissing() {
        when(fuelPriceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fuelPriceService.findById(99L))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateShouldModifyNameAndPriceWhenValid() {
        FuelPrice existing = FuelPrice.builder().id(1L).name("Gasoline").literPrice(new BigDecimal("5.899")).build();
        FuelPriceRequest request = new FuelPriceRequest("Gasoline Grid", new BigDecimal("6.299"));

        when(fuelPriceRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(fuelPriceRepository.existsByNameIgnoreCaseAndIdNot("Gasoline Grid", 1L)).thenReturn(false);
        when(fuelPriceRepository.save(existing)).thenReturn(existing);

        FuelPriceResponse updated = fuelPriceService.update(1L, request);

        assertThat(updated.name()).isEqualTo("Gasoline Grid");
        assertThat(updated.literPrice()).isEqualByComparingTo("6.299");
    }

    @Test
    void deleteShouldRemoveExistingFuelPrice() {
        when(fuelPriceRepository.existsById(1L)).thenReturn(true);

        fuelPriceService.delete(1L);

        verify(fuelPriceRepository).deleteById(1L);
    }
}
