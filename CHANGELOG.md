# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial Spring Boot project structure with Java 25, PostgreSQL 17 (`db_posto`) via Docker Compose, and GitHub Actions CI pipeline.
- Repository governance files (`CONTRIBUTING.md`, `.editorconfig`, `CODEOWNERS`), GitHub templates (PR, Issues, Dependabot, Release Notes), and initial ADRs.
- Formal requirements analysis, user needs, and BDD/Gherkin acceptance criteria in `docs/requirements.md`.
- JPA domain entities (`FuelType`, `FuelPump`, `Refueling`) and Spring Data repositories (`FuelTypeRepository`, `FuelPumpRepository`, `RefuelingRepository`) under `br.com.lefmachado.gasstation` (#1).
- Request and response DTO records (`FuelTypeRequest`, `FuelTypeResponse`) for fuel type API contracts (#2).
- Business service layer (`FuelTypeService`) with duplicate name validation (`existsByNameIgnoreCase`, `existsByNameIgnoreCaseAndIdNot`) and pump dependency protection on deletion (#2).
- REST controller (`FuelTypeController`) exposing CRUD endpoints under `/api/v1/fuel-types` (#2).
- Request and response DTO records (`FuelPumpRequest`, `FuelPumpResponse`) for fuel pump API contracts (#3).
- Business service layer (`FuelPumpService`) validating linked fuel type existence and protecting deletion when refuelings are registered (`existsByFuelPumpId`) (#3).
- REST controller (`FuelPumpController`) exposing CRUD endpoints under `/api/v1/fuel-pumps` (#3).
- Multi-phase architectural evolution roadmap in `README.md` covering microservices extraction, event-driven resilience, and zero-trust identity/security.
- Request and response DTO records (`RefuelingRequest`, `RefuelingResponse`) for refueling API contracts (#4).
- Business service layer (`RefuelingService`) with exact `BigDecimal` liters/amount calculation, exclusive input validation, and historical `appliedLiterPrice` snapshotting (#4).
- REST controller (`RefuelingController`) exposing CRUD endpoints under `/api/v1/refuelings` (#4).
- Bean Validation (`spring-boot-starter-validation`) on request DTOs (`FuelTypeRequest`, `FuelPumpRequest`, `RefuelingRequest`) and `@Valid` enforcement across all REST controllers (#5).
- Global exception handler (`GlobalExceptionHandler` with `@RestControllerAdvice`) returning standardized RFC 7807 `ProblemDetail` payloads for validation, HTTP status, malformed JSON, type mismatch, and unexpected errors (#5).
- Versioned database schema migrations with Flyway (`V1__create_initial_schema.sql`), replacing Hibernate `ddl-auto=update` with `ddl-auto=validate` (#18).
- Unit test suite with JUnit 5 and Mockito (`FuelTypeServiceTest`, `FuelPumpServiceTest`, `RefuelingServiceTest`) and Spring Boot integration tests (`GasStationApiIntegrationTest`) covering CRUD flows, automatic calculation, historical price immutability, referential integrity, and RFC 7807 validation/error handling (#6).

[Unreleased]: https://github.com/lefmachado/gas-station-api/compare/main...develop