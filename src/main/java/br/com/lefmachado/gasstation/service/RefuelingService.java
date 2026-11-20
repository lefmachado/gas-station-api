package br.com.lefmachado.gasstation.service;

import br.com.lefmachado.gasstation.dto.RefuelingRequest;
import br.com.lefmachado.gasstation.dto.RefuelingResponse;
import br.com.lefmachado.gasstation.entity.FuelPump;
import br.com.lefmachado.gasstation.entity.Refueling;
import br.com.lefmachado.gasstation.repository.FuelPumpRepository;
import br.com.lefmachado.gasstation.repository.RefuelingRepository;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class RefuelingService {

    private static final int LITERS_SCALE = 3;
    private static final int AMOUNT_SCALE = 2;

    private final RefuelingRepository refuelingRepository;
    private final FuelPumpRepository fuelPumpRepository;

    @Transactional
    public RefuelingResponse create(RefuelingRequest request) {
        FuelPump fuelPump = findFuelPumpOrThrow(request.fuelPumpId());
        BigDecimal appliedLiterPrice = fuelPump.getFuelType().getLiterPrice();

        CalculatedAmounts amounts = calculateAmounts(request, appliedLiterPrice);

        Refueling refueling = Refueling.builder()
                .fuelPump(fuelPump)
                .refueledAt(request.refueledAt())
                .liters(amounts.liters())
                .totalAmount(amounts.totalAmount())
                .appliedLiterPrice(appliedLiterPrice)
                .build();

        Refueling saved = refuelingRepository.save(refueling);
        return RefuelingResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<RefuelingResponse> findAll() {
        return refuelingRepository.findAll()
                .stream()
                .map(RefuelingResponse::fromEntity)
                .toList();
    }

    @Transactional(readOnly = true)
    public RefuelingResponse findById(Long id) {
        return refuelingRepository.findById(id)
                .map(RefuelingResponse::fromEntity)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Refueling not found with id: " + id));
    }

    @Transactional
    public RefuelingResponse update(Long id, RefuelingRequest request) {
        Refueling existing = refuelingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Refueling not found with id: " + id));

        FuelPump fuelPump = findFuelPumpOrThrow(request.fuelPumpId());

        boolean sameFuelType = fuelPump.getFuelType().getId()
                .equals(existing.getFuelPump().getFuelType().getId());

        BigDecimal appliedLiterPrice = sameFuelType
                ? existing.getAppliedLiterPrice()
                : fuelPump.getFuelType().getLiterPrice();

        CalculatedAmounts amounts = calculateAmounts(request, appliedLiterPrice);

        existing.setFuelPump(fuelPump);
        if (request.refueledAt() != null) {
            existing.setRefueledAt(request.refueledAt());
        }
        existing.setLiters(amounts.liters());
        existing.setTotalAmount(amounts.totalAmount());
        existing.setAppliedLiterPrice(appliedLiterPrice);

        Refueling updated = refuelingRepository.save(existing);
        return RefuelingResponse.fromEntity(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!refuelingRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Refueling not found with id: " + id);
        }

        refuelingRepository.deleteById(id);
    }

    private FuelPump findFuelPumpOrThrow(Long fuelPumpId) {
        if (fuelPumpId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Fuel pump ID is required");
        }

        return fuelPumpRepository.findById(fuelPumpId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Fuel pump not found with id: " + fuelPumpId));
    }

    private CalculatedAmounts calculateAmounts(
            RefuelingRequest request,
            BigDecimal appliedLiterPrice) {

        boolean hasLiters = request.liters() != null;
        boolean hasTotalAmount = request.totalAmount() != null;

        if (hasLiters == hasTotalAmount) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provide either 'liters' or 'totalAmount', but not both");
        }

        if (hasTotalAmount) {
            if (request.totalAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Total amount must be greater than zero");
            }
            BigDecimal totalAmount = request.totalAmount()
                    .setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
            BigDecimal liters = totalAmount
                    .divide(appliedLiterPrice, LITERS_SCALE, RoundingMode.HALF_UP);
            return new CalculatedAmounts(liters, totalAmount);
        }

        if (request.liters().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Liters must be greater than zero");
        }
        BigDecimal liters = request.liters()
                .setScale(LITERS_SCALE, RoundingMode.HALF_UP);
        BigDecimal totalAmount = liters
                .multiply(appliedLiterPrice)
                .setScale(AMOUNT_SCALE, RoundingMode.HALF_UP);
        return new CalculatedAmounts(liters, totalAmount);
    }

    private record CalculatedAmounts(BigDecimal liters, BigDecimal totalAmount) {
    }
}