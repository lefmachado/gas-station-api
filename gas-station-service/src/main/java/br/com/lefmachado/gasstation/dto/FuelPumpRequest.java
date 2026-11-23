package br.com.lefmachado.gasstation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record FuelPumpRequest(
        @NotBlank(message = "Fuel pump name is required")
        @Size(max = 100, message = "Fuel pump name must be at most 100 characters")
        String name,

        @NotNull(message = "Fuel type ID is required")
        @Positive(message = "Fuel type ID must be positive")
        Long fuelTypeId) {
}
