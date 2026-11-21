# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [1.0.0] - 2026-11-21

### Added
- Initial Spring Boot project structure with Java 25, PostgreSQL 17 (`db_posto`) via Docker Compose, and GitHub Actions CI pipeline.
- Repository governance files (`CONTRIBUTING.md`, `.editorconfig`, `CODEOWNERS`), GitHub templates (PR, Issues, Dependabot, Release Notes), and initial ADRs (`docs/adr/0001`–`0005`).
- Formal requirements analysis, user needs, and BDD/Gherkin acceptance criteria in `docs/requirements.md`.
- Multi-phase architectural evolution roadmap and reference blueprints in `README.md` covering microservices extraction, event-driven resilience, observability, zero-trust identity/security, and AI operational copilot (Phases 2–5).
- JPA domain entities (`FuelType`, `FuelPump`, `Refueling`) and Spring Data repositories (`FuelTypeRepository`, `FuelPumpRepository`, `RefuelingRepository`) under `br.com.lefmachado.gasstation` (#1).
- Fuel Types CRUD (`/api/v1/fuel-types`): DTO records (`FuelTypeRequest`, `FuelTypeResponse`), `FuelTypeService` with duplicate name validation and pump dependency protection on deletion, and `FuelTypeController` (#2).
- Fuel Pumps CRUD (`/api/v1/fuel-pumps`): DTO records (`FuelPumpRequest`, `FuelPumpResponse`), `FuelPumpService` validating linked fuel types and protecting deletion when refuelings exist, and `FuelPumpController` (#3).
- Refuelings CRUD (`/api/v1/refuelings`): DTO records (`RefuelingRequest`, `RefuelingResponse`), `RefuelingService` with exact `BigDecimal` liters/amount calculation, exclusive input validation, and historical `appliedLiterPrice` snapshotting, and `RefuelingController` (#4).
- Bean Validation (`spring-boot-starter-validation`) on request DTOs (`FuelTypeRequest`, `FuelPumpRequest`, `RefuelingRequest`) and `@Valid` enforcement across all REST controllers (#5).
- Global exception handler (`GlobalExceptionHandler` with `@RestControllerAdvice`) returning standardized RFC 7807 `ProblemDetail` payloads for validation, HTTP status, malformed JSON, type mismatch, and unexpected errors (#5).
- Versioned database schema migrations with Flyway (`V1__create_initial_schema.sql`), replacing Hibernate `ddl-auto=update` with `ddl-auto=validate` (#18).
- Unit test suite with JUnit 5 and Mockito (`FuelTypeServiceTest`, `FuelPumpServiceTest`, `RefuelingServiceTest`) and Spring Boot integration tests (`GasStationApiIntegrationTest`) covering CRUD flows, automatic calculation, historical price immutability, referential integrity, and RFC 7807 validation/error handling (#6).

[Unreleased]: https://github.com/lefmachado/gas-station-api/compare/v1.0.0...develop
[1.0.0]: https://github.com/lefmachado/gas-station-api/releases/tag/v1.0.0
