package br.com.lefmachado.gasstation.dto;

import br.com.lefmachado.gasstation.entity.FuelType;
import java.math.BigDecimal;

public record FuelTypeResponse(
        Long id,
        String name,
        BigDecimal literPrice) {
    public static FuelTypeResponse fromEntity(FuelType entity) {
        return new FuelTypeResponse(
                entity.getId(),
                entity.getName(),
                entity.getLiterPrice());
    }
}