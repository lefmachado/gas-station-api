package br.com.lefmachado.pricing.dto;

import br.com.lefmachado.pricing.entity.FuelPrice;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record FuelPriceResponse(
        Long id,
        String name,
        BigDecimal literPrice,
        OffsetDateTime updatedAt) {

    public static FuelPriceResponse fromEntity(FuelPrice fuelPrice) {
        return new FuelPriceResponse(
                fuelPrice.getId(),
                fuelPrice.getName(),
                fuelPrice.getLiterPrice(),
                fuelPrice.getUpdatedAt());
    }
}
