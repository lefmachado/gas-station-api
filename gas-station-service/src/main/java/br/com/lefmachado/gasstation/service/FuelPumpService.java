package br.com.lefmachado.gasstation.service;

import br.com.lefmachado.gasstation.dto.FuelPumpRequest;
import br.com.lefmachado.gasstation.dto.FuelPumpResponse;
import br.com.lefmachado.gasstation.entity.FuelPump;
import br.com.lefmachado.gasstation.entity.FuelType;
import br.com.lefmachado.gasstation.repository.FuelPumpRepository;
import br.com.lefmachado.gasstation.repository.FuelTypeRepository;
import br.com.lefmachado.gasstation.repository.RefuelingRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class FuelPumpService {

    private final FuelPumpRepository fuelPumpRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final RefuelingRepository refuelingRepository;

    @Transactional
    public FuelPumpResponse create(FuelPumpRequest request) {
        FuelType fuelType = findFuelTypeOrThrow(request.fuelTypeId());

        FuelPump fuelPump = FuelPump.builder()
                .name(request.name().trim())
                .fuelType(fuelType)
                .build();

        FuelPump saved = fuelPumpRepository.save(fuelPump);
        return FuelPumpResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<FuelPumpResponse> findAll() {
        return fuelPumpRepository.findAll()
                .stream()
                .map(FuelPumpResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public FuelPumpResponse findById(Long id) {
        return fuelPumpRepository.findById(id)
                .map(FuelPumpResponse::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Fuel pump not found with id: " + id));
    }

    @Transactional
    public FuelPumpResponse update(Long id, FuelPumpRequest request) {
        FuelPump existing = fuelPumpRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Fuel pump not found with id: " + id));

        FuelType fuelType = findFuelTypeOrThrow(request.fuelTypeId());

        existing.setName(request.name().trim());
        existing.setFuelType(fuelType);

        FuelPump updated = fuelPumpRepository.save(existing);
        return FuelPumpResponse.fromEntity(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!fuelPumpRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Fuel pump not found with id: " + id);
        }

        if (refuelingRepository.existsByFuelPumpId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete fuel pump because it has registered refuelings");
        }

        fuelPumpRepository.deleteById(id);
    }

    private FuelType findFuelTypeOrThrow(Long fuelTypeId) {
        return fuelTypeRepository.findById(fuelTypeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Fuel type not found with id: " + fuelTypeId));
    }
}