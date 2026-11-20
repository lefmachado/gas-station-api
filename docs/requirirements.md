# 📋 Requirements & Problem Analysis — Gas Station API

This document formalizes the problem space, user needs, functional/non-functional requirements, constraints, and acceptance criteria guiding the evolution of `gas-station-api` from a Modular Monolith (Phase 1) to a resilient, event-driven, zero-trust distributed system (Phases 2–4).

---

## 1. Problem Space, Workflow & Digital Solution

| Dimension | Context in the Gas Station Ecosystem |
| :--- | :--- |
| **Public / Business Problem** | Lack of transparency in fuel price updates, discrepancies between prices displayed on totems/signs and prices charged at the pump, cash-register closing errors, and vulnerability to internal fraud or fiscal inconsistency. |
| **Workflow (*As-Is* $\rightarrow$ *To-Be*)** | **As-Is:** Manual price changes on signs and pumps at different times, manual volume/amount calculations at the forecourt, and error-prone spreadsheet reconciliation.<br>**To-Be:** Fuel prices maintained in a single source of truth (`pricing-service`), automatically replicated to pumps and totems via events, with automated calculation and immutable refueling history. |
| **Digital Solution** | Standardized RESTful APIs, relational persistence with financial precision and historical price snapshots, asynchronous event-driven fault tolerance (forecourt never stops during outages), and Role-Based Access Control with Segregation of Duties. |

---

## 2. User Needs & Personas

- **Forecourt Attendant (`ROLE_ATTENDANT`):** Operates under queue pressure. Needs to register a refueling in seconds by informing only the pump and either the requested monetary amount (e.g., R$ 100.00) or the volume in liters—without manual math or typing unit prices.
- **Station Manager (`ROLE_STATION_MANAGER`):** Needs accurate daily cash-register closing and auditable historical records. Future fuel price increases must never retroactively alter past refueling totals.
- **Consumer / Digital Totem (`SCOPE_prices:read`):** Needs real-time, low-latency access to current fuel prices and accurate receipts showing exact liters, applied unit price, and total amount.
- **Pricing Analyst & Auditors (`ROLE_PRICING_ADMIN`):** Needs centralized price governance with clear audit trails, ensuring pricing administrators cannot register forecourt refuelings (Segregation of Duties).

---

## 3. Functional Requirements (FR)

- **FR01 — Fuel Types Management:** Create, list, retrieve, update, and delete fuel types with case-insensitive unique names and unit prices per liter.
- **FR02 — Fuel Pumps Management:** Create, list, retrieve, update, and delete fuel pumps associated with a valid fuel type. Pump names are not globally unique to support multi-branch scalability (`station_id` + `name` uniqueness in future phases).
- **FR03 — Refueling Registration & Automatic Calculation:** Register refuelings linked to a valid fuel pump, automatically calculating `liters` from `totalAmount` (or `totalAmount` from `liters`) using the pump's current fuel price.
- **FR04 — Historical Price Snapshot:** Persist `appliedLiterPrice` at the exact moment of refueling so subsequent fuel price changes never affect historical records.
- **FR05 — Referential Integrity Protection:** Block deletion of a fuel type linked to existing pumps (`409 Conflict`) and block deletion of a fuel pump linked to existing refuelings (`409 Conflict`).

---

## 4. Non-Functional Requirements (NFR)

- **NFR01 — Financial Precision:** All monetary values and volumes must use exact decimal arithmetic (`BigDecimal` in Java, `NUMERIC(10,3)` / `NUMERIC(10,2)` in PostgreSQL)—never binary floating-point (`float`/`double`).
- **NFR02 — Consistency & Integrity:** Enforce referential integrity in both the service layer (semantic HTTP `404`/`409` responses) and database layer (PostgreSQL foreign keys).
- **NFR03 — Human-Centered Design & API Ergonomics:** Minimize operator cognitive load (single-input calculation for refuelings), return self-contained DTOs (nested `fuelType` inside `FuelPumpResponse`), and provide actionable error payloads following RFC 7807 (`ProblemDetail`).
- **NFR04 — Fault Tolerance & Availability (Phase 3):** Refueling registration must remain operational even if `pricing-service` is offline, reading from a locally replicated read model synchronized asynchronously via RabbitMQ and Transactional Outbox.
- **NFR05 — Security & Zero-Trust Governance (Phase 4):** Stateless OAuth 2.0 / OIDC authentication via Keycloak (`RS256` JWTs), Segregation of Duties (`@PreAuthorize`), BOLA/IDOR branch ownership checks (`station_id`), and least-privilege RabbitMQ service accounts.

---

## 5. Constraints

- **Technical Constraints:** Java 25, Spring Boot 4.x, Spring Data JPA / Hibernate, PostgreSQL 17 running via Docker Compose, stateless JSON REST API under `/api/v1/**`.
- **Domain Constraints:** `literPrice`, `liters`, and `totalAmount` must be strictly greater than zero (`> 0`); `appliedLiterPrice` is immutable once persisted.

---

## 6. Core Acceptance Criteria (BDD / Gherkin)

> *Detailed, feature-specific acceptance criteria are tracked directly inside each GitHub Issue and validated via automated tests.*

### Feature: Refueling Registration & Historical Integrity (Issue #4)

```gherkin
Scenario 1: Register refueling informing only totalAmount
  Given Fuel Pump 1 dispenses "Gasoline" priced at 5.899 per liter
  When a client sends POST /api/v1/refuelings with pumpId=1 and totalAmount=100.00
  Then the system calculates liters=16.952 (HALF_UP rounding, 3 decimal places)
  And persists appliedLiterPrice=5.899 on the refueling record
  And returns HTTP 201 Created with a Location header

Scenario 2: Register refueling informing only liters
  Given Fuel Pump 1 dispenses "Gasoline" priced at 5.899 per liter
  When a client sends POST /api/v1/refuelings with pumpId=1 and liters=20.000
  Then the system calculates totalAmount=117.98 (HALF_UP rounding, 2 decimal places)
  And persists appliedLiterPrice=5.899 on the refueling record
  And returns HTTP 201 Created

Scenario 3: Historical immutability after price update
  Given a registered refueling with appliedLiterPrice=5.899 and totalAmount=100.00
  When "Gasoline" price is updated to 6.299 per liter via PUT /api/v1/fuel-types/{id}
  Then GET /api/v1/refuelings/{id} must still return appliedLiterPrice=5.899 and totalAmount=100.00
```

### Feature: Referential Integrity Protection (Issues #2 & #3)

```gherkin
Scenario 4: Prevent deleting a fuel type linked to a pump
  Given Fuel Type 2 ("Gasoline") is linked to Fuel Pump 1
  When a client sends DELETE /api/v1/fuel-types/2
  Then the API rejects the operation with HTTP 409 Conflict
```