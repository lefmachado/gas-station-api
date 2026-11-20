package br.com.lefmachado.gasstation.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record RefuelingRequest(
        Long fuelPumpId,
        LocalDateTime refueledAt,
        BigDecimal liters,
        BigDecimal totalAmount) {
}