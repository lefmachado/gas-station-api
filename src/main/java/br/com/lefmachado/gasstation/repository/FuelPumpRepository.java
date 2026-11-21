package br.com.lefmachado.gasstation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lefmachado.gasstation.entity.FuelPump;

public interface FuelPumpRepository extends JpaRepository<FuelPump, Long> {
    boolean existsByFuelTypeId(Long fuelTypeId);
}
