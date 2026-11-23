# 1. Use PostgreSQL 17 and NUMERIC/BigDecimal for Monetary Precision

- **Status:** Accepted
- **Date:** 2026-11-18

## Context
Fuel unit prices (commonly expressed with three decimal places in Brazil, e.g., `R$ 5.899`) and refueling monetary totals require exact arithmetic without binary floating-point rounding errors.

## Decision
Use PostgreSQL 17 as the primary relational database (`db_gas_station`) and map monetary and volume fields using `NUMERIC(10,3)` / `NUMERIC(10,2)` in SQL paired with `java.math.BigDecimal` in Java entities and services.

## Consequences
- Prevents precision loss in financial calculations.
- Enforces referential integrity (`1:N` relationships between fuel types, pumps, and refuelings) natively at the database level.
