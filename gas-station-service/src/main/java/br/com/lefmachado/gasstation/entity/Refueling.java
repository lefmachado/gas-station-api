package br.com.lefmachado.gasstation.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "refuelings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Refueling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fuel_pump_id", nullable = false)
    private FuelPump fuelPump;

    @Column(name = "refueled_at", nullable = false)
    private LocalDateTime refueledAt;

    @Column(name = "liters", nullable = false, precision = 10, scale = 3)
    private BigDecimal liters;

    @Column(name = "applied_liter_price", nullable = false, precision = 10, scale = 3)
    private BigDecimal appliedLiterPrice;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @PrePersist
    public void prePersist() {
        if (this.refueledAt == null) {
            this.refueledAt = LocalDateTime.now();
        }
    }
}