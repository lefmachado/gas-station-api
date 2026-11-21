package br.com.lefmachado.gasstation.dto;

import br.com.lefmachado.gasstation.entity.FuelPump;

public record FuelPumpResponse(
        Long id,
        String name,
        FuelTypeResponse fuelType) {

    public static FuelPumpResponse fromEntity(FuelPump entity) {
        return new FuelPumpResponse(
                entity.getId(),
                entity.getName(),
                FuelTypeResponse.fromEntity(entity.getFuelType()));
    }
}