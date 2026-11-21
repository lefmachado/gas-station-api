package br.com.lefmachado.gasstation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RefuelingRequest(
        @NotNull(message = "Fuel pump ID is required")
        @Positive(message = "Fuel pump ID must be positive")
        Long fuelPumpId,

        @PastOrPresent(message = "Refueling date cannot be in the future")
        LocalDateTime refueledAt,

        @Positive(message = "Liters must be greater than zero")
        BigDecimal liters,

        @Positive(message = "Total amount must be greater than zero")
        BigDecimal totalAmount) {
}
