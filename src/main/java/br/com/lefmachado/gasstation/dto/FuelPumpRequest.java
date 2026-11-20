package br.com.lefmachado.gasstation.dto;

public record FuelPumpRequest(
        String name,
        Long fuelTypeId) {
}
