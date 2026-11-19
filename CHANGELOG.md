# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial Spring Boot project structure with Java 25, PostgreSQL 17 (`db_posto`) via Docker Compose, and GitHub Actions CI pipeline.
- Repository governance files (`CONTRIBUTING.md`, `.editorconfig`, `CODEOWNERS`), GitHub templates (PR, Issues, Dependabot, Release Notes), and initial ADRs.
- JPA domain entities (`FuelType`, `FuelPump`, `Refueling`) and Spring Data repositories (`FuelTypeRepository`, `FuelPumpRepository`, `RefuelingRepository`) under `br.com.lefmachado.gasstation` (#1).
- Request and response DTO records (`FuelTypeRequest`, `FuelTypeResponse`) for fuel type API contracts (#2).
- Business service layer (`FuelTypeService`) with duplicate name validation (`existsByNameIgnoreCase`, `existsByNameIgnoreCaseAndIdNot`) and pump dependency protection on deletion (#2).
- REST controller (`FuelTypeController`) exposing CRUD endpoints under `/api/v1/fuel-types` (#2).

[Unreleased]: https://github.com/lefmachado/gas-station-api/compare/main...develop