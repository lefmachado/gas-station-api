# 2. Store Applied Liter Price Snapshot in Refueling Records

- **Status:** Accepted
- **Date:** 2026-11-18

## Context
Fuel prices change frequently. If a refueling record only references the current price in `tipos_combustivel`, historical refueling records would have their unit price altered retroactively whenever fuel prices are updated.

## Decision
Persist `preco_litro_aplicado` directly inside the `abastecimentos` table at the moment the refueling is registered.

## Consequences
- Preserves accurate historical auditability for every refueling transaction.
- Allows recalculating or verifying historical totals independently of future price changes.
