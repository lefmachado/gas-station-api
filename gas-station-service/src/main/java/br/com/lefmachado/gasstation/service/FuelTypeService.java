package br.com.lefmachado.gasstation.service;

import br.com.lefmachado.gasstation.dto.FuelTypeRequest;
import br.com.lefmachado.gasstation.dto.FuelTypeResponse;
import br.com.lefmachado.gasstation.entity.FuelType;
import br.com.lefmachado.gasstation.repository.FuelPumpRepository;
import br.com.lefmachado.gasstation.repository.FuelTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FuelTypeService {

    private final FuelTypeRepository fuelTypeRepository;
    private final FuelPumpRepository fuelPumpRepository;

    @Transactional
    public FuelTypeResponse create(FuelTypeRequest request) {
        String normalizedName = request.name().trim();

        if (fuelTypeRepository.existsByNameIgnoreCase(normalizedName)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Fuel type already exists with name: " + normalizedName);
        }

        FuelType fuelType = FuelType.builder()
                .name(normalizedName)
                .literPrice(request.literPrice())
                .build();

        FuelType saved = fuelTypeRepository.save(fuelType);
        return FuelTypeResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<FuelTypeResponse> findAll() {
        return fuelTypeRepository.findAll()
                .stream()
                .map(FuelTypeResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public FuelTypeResponse findById(Long id) {
        FuelType fuelType = fuelTypeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Fuel type not found with id: " + id));

        return FuelTypeResponse.fromEntity(fuelType);
    }

    @Transactional
    public FuelTypeResponse update(Long id, FuelTypeRequest request) {
        FuelType existing = fuelTypeRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Fuel type not found with id: " + id));

        String normalizedName = request.name().trim();

        if (fuelTypeRepository.existsByNameIgnoreCaseAndIdNot(normalizedName, id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Another fuel type already uses the name: " + normalizedName);
        }

        existing.setName(normalizedName);
        existing.setLiterPrice(request.literPrice());

        FuelType updated = fuelTypeRepository.save(existing);
        return FuelTypeResponse.fromEntity(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!fuelTypeRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Fuel type not found with id: " + id);
        }

        if (fuelPumpRepository.existsByFuelTypeId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete fuel type because it is linked to one or more fuel pumps");
        }

        fuelTypeRepository.deleteById(id);
    }
}