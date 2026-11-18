# ⛽ Gas Station API

![Java](https://img.shields.io/badge/Java-25-orange?style=flat-square&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-brightgreen?style=flat-square&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-blue?style=flat-square&logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=flat-square&logo=docker)
![Status](https://img.shields.io/badge/Status-Work%20in%20Progress-yellow?style=flat-square)

> 🚧 **Development Status:** This project is currently under active development. The sections below contain the full original challenge specification followed by the architectural decisions, database modeling, and implementation progress.

---

## 📄 Original Challenge Specification

### Junior Technical Challenge #1 – Refueling Registration and Query

#### 🎯 Objective
Develop a simple Java application for registering and querying refuelings at a gas station, with database storage and data display via Java Swing or REST API.

#### ⚙️ Required Features
* **Basic operations (Create, List, Update, Delete) for Fuel Types:**
  * Name (Text)
  * Price per liter
* **Basic operations (Create, List, Update, Delete) for Fuel Pumps** (related to a fuel type):
  * Pump name
  * Fuel type it dispenses
* **Basic operations (Create, List, Update, Delete) for Refuelings** (with date, refueled volume, and total value):
  * Pump where the refueling was performed
  * Refueling date
  * Monetary amount
  * Volume in liters
* **Query all registered data** (via Java Swing or API)
* **Data persistence** (at least at runtime)

#### ✅ Requirements
* Java project with an organized structure (using Maven or Gradle)
* Entity relationships properly implemented
* Java Swing GUI or HTTP API for registration and querying
* Commented and well-organized code

#### 🌟 Bonus / Differentials
* Simple RESTful API with `GET`, `POST`, `PUT` (and `DELETE`) routes
* Code organization best practices (Repository/DAO, Service layer, DTOs, etc.)

#### 📊 Evaluation Criteria
* Communication, especially when doubts or obstacles arise during development.
* The development process as a whole, not just the final result.
* Clarity and organization of the commits made.
* Ability to structure the solution in stages, even if not all requirements are completed.

#### 💡 Guidelines
* Break the challenge into small parts and implement calmly, focusing on one feature at a time.
* Use clear and objective commits, indicating exactly what was changed or implemented.
* When in doubt, communicate — demonstrating the ability to seek solutions is valued.
* Process quality is taken into account throughout the implementation.

---

## 🏗️ How It Was Implemented

### 1. Tech Stack & Architecture
* **Java 25 & Spring Boot:** Core backend framework providing embedded web server, dependency injection, and auto-configuration.
* **Spring Data JPA & Hibernate:** Object-Relational Mapping (ORM) and repository abstraction.
* **PostgreSQL 17:** Relational database (`db_posto`) chosen to enforce native foreign key integrity (`1:N` relationships) and financial precision.
* **Docker & Spring Boot Docker Compose:** Automated container lifecycle management with named volumes (`postgres_data`) so data persists across application restarts.
* **Lombok & Maven:** Boilerplate reduction and dependency/build management.

### 2. Relational Database Modeling
* **`tipos_combustivel` (Fuel Types):** Stores `id`, unique `nome`, and `preco_por_litro` (`NUMERIC(10,3)`).
* **`bombas_combustivel` (Fuel Pumps):** Stores `id`, `nome`, and a foreign key `tipo_combustivel_id` referencing `tipos_combustivel(id)`.
* **`abastecimentos` (Refuelings):** Stores `id`, foreign key `bomba_id`, `data_abastecimento`, `litragem`, `valor_total`, and a snapshot of `preco_litro_aplicado` at the exact time of refueling.

### 3. Layer Structure *(In Progress)*
```text
src/main/java/br/com/lefmachado/gasstationapi
├── controller/   # REST endpoints (GET, POST, PUT, DELETE)
├── service/      # Business rules (automatic liters/total value calculation & validations)
├── repository/   # Spring Data JPA interfaces
├── entity/       # JPA @Entity mappings
├── dto/          # Request/Response records for API contracts
└── exception/    # Global exception handling (@RestControllerAdvice)
```

---

## 🚀 Implementation Roadmap

- [x] Project scaffolding with Spring Initializr (Maven, Java 25, JPA, PostgreSQL, Lombok)
- [x] PostgreSQL container setup (`db_posto`) with persistent named volume in `compose.yaml`
- [x] Repository initialization, CI workflow, and documentation structure
- [x] Repository governance (`CONTRIBUTING.md`, `.editorconfig`, `CODEOWNERS`), GitHub templates, `CHANGELOG.md`, and initial ADRs (`docs/adr/`)
- [ ] Domain entities (`TipoCombustivel`, `BombaCombustivel`, `Abastecimento`) & Repositories
- [ ] Fuel Types CRUD endpoints and service rules
- [ ] Fuel Pumps CRUD endpoints linked to Fuel Types
- [ ] Refuelings CRUD endpoints with automatic volume/amount calculation
- [ ] Global exception handler & Bean Validation
- [ ] Unit and integration tests

---

## 💻 Running the Project Locally

### Prerequisites
- **Java 25+**
- **Docker** and **Docker Compose**

### Steps
1. **Clone the repository:**
   ```bash
   git clone https://github.com/lefmachado/gas-station-api.git
   cd gas-station-api
   ```

2. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```
   > *Spring Boot automatically detects `compose.yaml`, starts the PostgreSQL container, creates the `db_posto` database, and configures the JDBC connection.*