package br.com.lefmachado.gasstation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lefmachado.gasstation.entity.Refueling;

public interface RefuelingRepository extends JpaRepository<Refueling, Long> {
    boolean existsByFuelPumpId(Long fuelPumpId);
}
