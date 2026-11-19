package br.com.lefmachado.gasstation.dto;

import java.math.BigDecimal;

public record FuelTypeRequest(
        String name,
        BigDecimal literPrice) {
}
