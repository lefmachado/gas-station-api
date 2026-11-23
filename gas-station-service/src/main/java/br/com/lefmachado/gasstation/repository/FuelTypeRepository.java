package br.com.lefmachado.gasstation.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.lefmachado.gasstation.entity.FuelType;

public interface FuelTypeRepository extends JpaRepository<FuelType, Long> {
    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
