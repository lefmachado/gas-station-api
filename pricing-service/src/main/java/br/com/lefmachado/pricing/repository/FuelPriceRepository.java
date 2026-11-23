package br.com.lefmachado.pricing.repository;

import br.com.lefmachado.pricing.entity.FuelPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FuelPriceRepository extends JpaRepository<FuelPrice, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
