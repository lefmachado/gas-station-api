# ⛽ Gas Station API

![Java](https://img.shields.io/badge/Java-25-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-brightgreen?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?style=flat-square&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker)
![Status](https://img.shields.io/badge/Status-Work%20in%20Progress-yellow?style=flat-square)

> 🚧 **Development Status:** This project is currently under active development. The sections below contain the full original challenge specification followed by the architectural decisions, database modeling, and implementation progress.

---

## 📚 Project Documentation

- **[Requirements & Problem Analysis](docs/requirements.md):** User needs, personas, functional/non-functional requirements (FR/NFR), constraints, workflow analysis (*As-Is* $\rightarrow$ *To-Be*), and BDD/Gherkin acceptance criteria.
- **[Architecture Decision Records (ADRs)](docs/adr/):** Documented technical and architectural choices made across the project lifecycle.
- **[Changelog](CHANGELOG.md):** Chronological log of notable changes per release and issue.
- **[Contributing Guidelines](CONTRIBUTING.md):** Branching strategy, commit conventions, and PR workflow.

---

## 📄 Original Challenge Specification

### Junior Technical Challenge #1 – Refueling Registration and Query

#### 🎯 Objective
Develop a simple Java application for registering and querying refuelings at a gas station, with database storage and data display via Java Swing or REST API.

#### ⚙️ Required Features
- **Basic operations (Create, List, Update, Delete) for Fuel Types:**
  - Name (Text)
  - Price per liter
- **Basic operations (Create, List, Update, Delete) for Fuel Pumps** (related to a fuel type):
  - Pump name
  - Fuel type it dispenses
- **Basic operations (Create, List, Update, Delete) for Refuelings** (with date, refueled volume, and total value):
  - Pump where the refueling was performed
  - Refueling date
  - Monetary amount
  - Volume in liters
- **Query all registered data** (via Java Swing or API)
- **Data persistence** (at least at runtime)

#### ✅ Requirements
- Java project with an organized structure (using Maven or Gradle)
- Entity relationships properly implemented
- Java Swing GUI or HTTP API for registration and querying
- Commented and well-organized code

#### 🌟 Bonus / Differentials
- Simple RESTful API with `GET`, `POST`, `PUT` (and `DELETE`) routes
- Code organization best practices (Repository/DAO, Service layer, DTOs, etc.)

#### 📊 Evaluation Criteria
- Communication, especially when doubts or obstacles arise during development.
- The development process as a whole, not just the final result.
- Clarity and organization of the commits made.
- Ability to structure the solution in stages, even if not all requirements are completed.

#### 💡 Guidelines
- Break the challenge into small parts and implement calmly, focusing on one feature at a time.
- Use clear and objective commits, indicating exactly what was changed or implemented.
- When in doubt, communicate — demonstrating the ability to seek solutions is valued.
- Process quality is taken into account throughout the implementation.

---

## 🏗️ How It Was Implemented

### 1. Tech Stack & Architecture
- **Java 25 & Spring Boot:** Core backend framework providing embedded web server, dependency injection, and auto-configuration.
- **Spring Data JPA & Hibernate:** Object-Relational Mapping (ORM) and repository abstraction.
- **PostgreSQL 17:** Relational database (`db_posto`) chosen to enforce native foreign key integrity (`1:N` relationships) and financial precision.
- **Docker & Spring Boot Docker Compose:** Automated container lifecycle management with named volumes (`postgres_data`) so data persists across application restarts.
- **Lombok & Maven:** Boilerplate reduction and dependency/build management.

### 2. Relational Database Modeling
- **`fuel_types` (Fuel Types):** Stores `id`, unique `name`, and `liter_price` (`NUMERIC(10,3)`).
- **`fuel_pumps` (Fuel Pumps):** Stores `id`, `name`, and a foreign key `fuel_type_id` referencing `fuel_types(id)`.
- **`refuelings` (Refuelings):** Stores `id`, foreign key `fuel_pump_id`, `refueling_date`, `liters`, `total_amount`, and a snapshot of `applied_liter_price` at the exact time of refueling.

### 3. Layer Structure *(In Progress)*
```text
src/main/java/br/com/lefmachado/gasstation
├── controller/   # REST endpoints (GET, POST, PUT, DELETE)
├── service/      # Business rules (automatic liters/total value calculation & validations)
├── repository/   # Spring Data JPA interfaces
├── entity/       # JPA @Entity mappings
├── dto/          # Request/Response records for API contracts
└── exception/    # Global exception handling (@RestControllerAdvice) - Planned
```

### 4. Implemented REST Endpoints

| Resource | Method | Endpoint | Description |
| :--- | :--- | :--- | :--- |
| **Fuel Types** | `POST` | `/api/v1/fuel-types` | Register a new fuel type |
| **Fuel Types** | `GET` | `/api/v1/fuel-types` | List all registered fuel types |
| **Fuel Types** | `GET` | `/api/v1/fuel-types/{id}` | Get a fuel type by ID |
| **Fuel Types** | `PUT` | `/api/v1/fuel-types/{id}` | Update an existing fuel type |
| **Fuel Types** | `DELETE` | `/api/v1/fuel-types/{id}` | Delete a fuel type (if not linked to pumps) |
| **Fuel Pumps** | `POST` | `/api/v1/fuel-pumps` | Register a new fuel pump linked to a fuel type |
| **Fuel Pumps** | `GET` | `/api/v1/fuel-pumps` | List all registered fuel pumps |
| **Fuel Pumps** | `GET` | `/api/v1/fuel-pumps/{id}` | Get a fuel pump by ID |
| **Fuel Pumps** | `PUT` | `/api/v1/fuel-pumps/{id}` | Update an existing fuel pump |
| **Fuel Pumps** | `DELETE` | `/api/v1/fuel-pumps/{id}` | Delete a fuel pump (if no refuelings exist) |

---

## 🚀 Implementation Roadmap

### Phase 1: Modular Monolith (Core Challenge Scope)
- [x] Project scaffolding with Spring Initializr (Maven, Java 25, JPA, PostgreSQL, Lombok)
- [x] PostgreSQL container setup (`db_posto`) with persistent named volume in `compose.yaml`
- [x] Repository initialization, CI workflow, and documentation structure
- [x] Repository governance (`CONTRIBUTING.md`, `.editorconfig`, `CODEOWNERS`), GitHub templates, `CHANGELOG.md`, and initial ADRs (`docs/adr/`)
- [x] Formal requirements analysis, user needs, and BDD acceptance criteria ([`docs/requirements.md`](docs/requirements.md))
- [x] Domain entities (`FuelType`, `FuelPump`, `Refueling`) & Repositories
- [x] Fuel Types CRUD endpoints and service rules
- [x] Fuel Pumps CRUD endpoints linked to Fuel Types
- [ ] Refuelings CRUD endpoints with automatic volume/amount calculation
- [ ] Global exception handler & Bean Validation
- [ ] Unit and integration tests

### Phase 2: Distributed Architecture & Microservices Evolution *(Post-Monolith Simulation)*
> Simulates scaling the system for a national network of gas stations and digital totems requiring high-throughput fuel price lookups without scaling the administrative module.

- [ ] Extract `pricing-service` as an independent Spring Boot microservice with its own PostgreSQL database (`db_pricing`) and `fuel_prices` table (*Database-per-Service* pattern)
- [ ] Add distributed caching (Redis) in `pricing-service` to serve high-concurrency price queries from digital totems and pumps
- [ ] Refactor `gas-station-api` monolith to remove the local `FuelType` entity/table, replacing `@ManyToOne` in `FuelPump` with a logical reference (`Long fuelTypeId`)
- [ ] Implement `PricingClient` using Spring `RestClient` in `gas-station-api` to fetch current fuel prices over HTTP during refueling registration
- [ ] Add synchronous resilience patterns (Circuit Breaker / Retry / Fallback) on HTTP calls between `gas-station-api` and `pricing-service`
- [ ] Orchestrate the multi-service environment (`gas-station-api`, `pricing-service`, `db_posto`, `db_pricing`, Redis) via Docker Compose

### Phase 3: Event-Driven Resilience & Fault Tolerance *(Outage Simulation)*
> Simulates network partitions and `pricing-service` outages, eliminating synchronous runtime dependencies during refueling via asynchronous messaging and local read-model replication.

- [ ] Add RabbitMQ broker (`rabbitmq:4-management`) to `compose.yaml` with durable Topic Exchanges, retry policies, and Dead Letter Queues (DLQ)
- [ ] Implement the Transactional Outbox Pattern in `pricing-service` (`outbox_events` table + scheduled relay) to guarantee atomic event publishing on price changes
- [ ] Implement idempotent RabbitMQ consumer in `gas-station-api` (`processed_events` + `replicated_fuel_prices` local read model)
- [ ] Refactor `RefuelingService` to read prices from the local replicated table instead of synchronous HTTP calls, keeping refueling operational even when `pricing-service` is offline
- [ ] Simulate `pricing-service` outage (`docker stop`) and validate zero-downtime refueling, queued event replay upon recovery, and DLQ handling with Testcontainers

### Phase 4: API Governance, Identity & Zero-Trust Security *(Access Control & Hardening Simulation)*
> Simulates multi-role station operations and machine-to-machine totem integrations, enforcing federated identity, Segregation of Duties (SoD), and least-privilege hardening across REST APIs and message brokers.

- [ ] Standardize API governance with OpenAPI 3 (`springdoc-openapi`) contracts, non-breaking URI versioning (`/api/v2/prices` expand-and-contract), and explicit `schemaVersion` envelopes on RabbitMQ events
- [ ] Add Keycloak Identity Provider to `compose.yaml` supporting OIDC + Authorization Code with PKCE (operators/managers) and OAuth 2.0 Client Credentials (`SCOPE_prices:read` for digital totems)
- [ ] Configure `pricing-service` and `gas-station-api` as stateless Spring Security OAuth2 Resource Servers validating asymmetric `RS256` JWTs via JWKS (`iss`, `aud`, and short-lived `exp` claims)
- [ ] Enforce Segregation of Duties (SoD) and defense-in-depth (`SecurityFilterChain` + `@PreAuthorize`) across `ROLE_PRICING_ADMIN`, `ROLE_ATTENDANT`, `ROLE_STATION_MANAGER`, and `ROLE_OPS_ADMIN`
- [ ] Prevent BOLA/IDOR vulnerabilities by validating branch ownership (`station_id` JWT claim) on pump and refueling operations, and lock down `/actuator/**` endpoints under a deny-by-default policy
- [ ] Harden RabbitMQ with least-privilege service accounts (`pricing_publisher` write-only on exchange; `gas_station_consumer` read-only on queue) and propagate immutable audit claims (`triggeredBySub`, `correlationId`) without passing expiring JWTs in messages

---

## 💻 Running the Project Locally

### Prerequisites
- **Java 25+**
- **Docker** and **Docker Compose**

### Steps
1. **Clone the repository:**
   ```bash
   git clone [https://github.com/lefmachado/gas-station-api.git](https://github.com/lefmachado/gas-station-api.git)
   cd gas-station-api
   ```

2. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```
   > *Spring Boot automatically detects `compose.yaml`, starts the PostgreSQL container, creates the `db_posto` database, and configures the JDBC connection.*