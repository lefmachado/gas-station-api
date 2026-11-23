package br.com.lefmachado.gasstation.dto;

import br.com.lefmachado.gasstation.entity.Refueling;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RefuelingResponse(
        Long id,
        FuelPumpResponse fuelPump,
        LocalDateTime refueledAt,
        BigDecimal liters,
        BigDecimal totalAmount,
        BigDecimal appliedLiterPrice) {

    public static RefuelingResponse fromEntity(Refueling entity) {
        return new RefuelingResponse(
                entity.getId(),
                FuelPumpResponse.fromEntity(entity.getFuelPump()),
                entity.getRefueledAt(),
                entity.getLiters(),
                entity.getTotalAmount(),
                entity.getAppliedLiterPrice());
    }
}