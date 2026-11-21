# 2. Store Applied Liter Price Snapshot in Refueling Records

- **Status:** Accepted
- **Date:** 2026-11-18

## Context
Fuel prices change frequently. If a refueling record only references the current price in `fuel_types`, historical refueling records would have their unit price altered retroactively whenever fuel prices are updated.

## Decision
Persist `applied_liter_price` (`appliedLiterPrice`) directly inside the `refuelings` table at the moment the refueling is registered.

## Consequences
- Preserves accurate historical auditability for every refueling transaction.
- Allows recalculating or verifying historical totals independently of future price changes.
