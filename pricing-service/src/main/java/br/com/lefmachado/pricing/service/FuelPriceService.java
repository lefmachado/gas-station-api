package br.com.lefmachado.pricing.service;

import br.com.lefmachado.pricing.dto.FuelPriceRequest;
import br.com.lefmachado.pricing.dto.FuelPriceResponse;
import br.com.lefmachado.pricing.entity.FuelPrice;
import br.com.lefmachado.pricing.repository.FuelPriceRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class FuelPriceService {

    private final FuelPriceRepository fuelPriceRepository;

    @Transactional
    public FuelPriceResponse create(FuelPriceRequest request) {
        String normalizedName = request.name().trim();

        if (fuelPriceRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Fuel price already exists with name: " + normalizedName);
        }

        FuelPrice fuelPrice = FuelPrice.builder()
                .name(normalizedName)
                .literPrice(request.literPrice())
                .build();

        FuelPrice saved = fuelPriceRepository.save(fuelPrice);
        return FuelPriceResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<FuelPriceResponse> findAll() {
        return fuelPriceRepository.findAll()
                .stream()
                .map(FuelPriceResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public FuelPriceResponse findById(Long id) {
        FuelPrice fuelPrice = fuelPriceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Fuel price not found with id: " + id));

        return FuelPriceResponse.fromEntity(fuelPrice);
    }

    @Transactional
    public FuelPriceResponse update(Long id, FuelPriceRequest request) {
        FuelPrice existing = fuelPriceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Fuel price not found with id: " + id));

        String normalizedName = request.name().trim();

        if (fuelPriceRepository.existsByNameIgnoreCaseAndIdNot(normalizedName, id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Another fuel price already uses the name: " + normalizedName);
        }

        existing.setName(normalizedName);
        existing.setLiterPrice(request.literPrice());

        FuelPrice updated = fuelPriceRepository.save(existing);
        return FuelPriceResponse.fromEntity(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!fuelPriceRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Fuel price not found with id: " + id);
        }

        fuelPriceRepository.deleteById(id);
    }
}
