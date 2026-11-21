package br.com.lefmachado.gasstation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record FuelTypeRequest(
        @NotBlank(message = "Fuel type name is required")
        @Size(max = 100, message = "Fuel type name must be at most 100 characters")
        String name,

        @NotNull(message = "Liter price is required")
        @DecimalMin(value = "0.001", message = "Liter price must be greater than zero")
        BigDecimal literPrice) {
}
