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

### Backend Technical Challenge – Refueling Registration and Query

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
- **`refuelings` (Refuelings):** Stores `id`, foreign key `fuel_pump_id`, `refueled_at`, `liters`, `total_amount`, and a snapshot of `applied_liter_price` at the exact time of refueling.

### 3. Layer Structure *(In Progress)*
```text
src/main/java/br/com/lefmachado/gasstation
├── controller/   # REST endpoints (GET, POST, PUT, DELETE)
├── service/      # Business rules (automatic liters/total value calculation & validations)
├── repository/   # Spring Data JPA interfaces
├── entity/       # JPA @Entity mappings
├── dto/          # Request/Response records for API contracts
└── exception/    # Global exception handling (@RestControllerAdvice + RFC 7807 ProblemDetail)
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
| **Refuelings** | `POST` | `/api/v1/refuelings` | Register a refueling with automatic calculation and price snapshot |
| **Refuelings** | `GET` | `/api/v1/refuelings` | List all registered refuelings |
| **Refuelings** | `GET` | `/api/v1/refuelings/{id}` | Get a refueling record by ID |
| **Refuelings** | `PUT` | `/api/v1/refuelings/{id}` | Update an existing refueling record |
| **Refuelings** | `DELETE` | `/api/v1/refuelings/{id}` | Delete a refueling record |

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
- [x] Refuelings CRUD endpoints with automatic volume/amount calculation
- [x] Global exception handler & Bean Validation
- [ ] Versioned database schema migrations with Flyway (`src/main/resources/db/migration`), replacing Hibernate `ddl-auto`
- [ ] Unit and integration tests

### Phase 2: Distributed Architecture & Microservices Evolution *(Post-Monolith Simulation)*
> Simulates scaling the system for a national network of gas stations and digital totems requiring high-throughput fuel price lookups without scaling the administrative module.

- [ ] Extract `pricing-service` as an independent Spring Boot microservice with its own PostgreSQL database (`db_pricing`), Flyway migrations, and `fuel_prices` table (*Database-per-Service* pattern)
- [ ] Add distributed caching (Redis) in `pricing-service` to serve high-concurrency price queries from digital totems and pumps, and benchmark `p95`/`p99` latency with and without Redis using k6 / JMeter
- [ ] Refactor `gas-station-api` monolith to remove the local `FuelType` entity/table, replacing `@ManyToOne` in `FuelPump` with a logical reference (`Long fuelTypeId`)
- [ ] Implement `PricingClient` using Spring `RestClient` in `gas-station-api` to fetch current fuel prices over HTTP during refueling registration
- [ ] Add synchronous resilience patterns (Resilience4j Circuit Breaker / Retry / Fallback) on HTTP calls between `gas-station-api` and `pricing-service`, plus HTTP `Idempotency-Key` header support on `POST /api/v1/refuelings` to prevent duplicate refueling charges on client retries
- [ ] Orchestrate the multi-service environment (`gas-station-api`, `pricing-service`, `db_posto`, `db_pricing`, Redis) with multi-stage `Dockerfile`s, `compose.yaml`, and reference Kubernetes manifests (`k8s/` Deployment, HPA, Liveness/Readiness Probes)

### Phase 3: Event-Driven Resilience, Observability & Fault Tolerance *(Outage Simulation)*
> Simulates network partitions and `pricing-service` outages, eliminating synchronous runtime dependencies during refueling via asynchronous messaging, local read-model replication, and full-stack observability.

- [ ] Add RabbitMQ broker (`rabbitmq:4-management`) to `compose.yaml` with durable Topic Exchanges, retry policies, and Dead Letter Queues (DLQ)
- [ ] Implement the Transactional Outbox Pattern in `pricing-service` (`outbox_events` table + scheduled relay) to guarantee atomic event publishing on price changes
- [ ] Implement idempotent RabbitMQ consumer in `gas-station-api` (`processed_events` + `replicated_fuel_prices` local read model) and an event-driven serverless function consumer simulation (e.g., fiscal receipt generation / price-change alert webhook)
- [ ] Refactor `RefuelingService` to read prices from the local replicated table instead of synchronous HTTP calls, keeping refueling operational even when `pricing-service` is offline
- [ ] Add full-stack Observability & Dependency Diagnostics: structured JSON logs (Logback), Spring Boot Actuator (`/actuator/health/liveness` and `/actuator/health/readiness` with PostgreSQL, Redis, and RabbitMQ Health Indicators), Micrometer metrics exported to Prometheus + Grafana, and OpenTelemetry distributed tracing (Jaeger / Grafana Tempo) propagating `traceId` across HTTP and RabbitMQ headers
- [ ] Simulate `pricing-service` outage (`docker stop`) and validate zero-downtime refueling, graceful degradation, queued event replay upon recovery, and DLQ reprocessing with Testcontainers

### Phase 4: API Governance, Identity & Zero-Trust Security *(Access Control & Hardening Simulation)*
> Simulates multi-role station operations and machine-to-machine totem integrations, enforcing federated identity, Segregation of Duties (SoD), contract evolution, and least-privilege hardening across REST APIs and message brokers.

- [ ] Standardize API & Schema Governance with OpenAPI 3 (`springdoc-openapi`) contracts, non-breaking URI versioning (`/api/v2/prices` expand-and-contract), explicit `schemaVersion` envelopes on RabbitMQ events, and zero-downtime Flyway schema evolution
- [ ] Add Keycloak Identity Provider to `compose.yaml` supporting OIDC + Authorization Code with PKCE (operators/managers) and OAuth 2.0 Client Credentials (`SCOPE_prices:read` for digital totems)
- [ ] Configure `pricing-service` and `gas-station-api` as stateless Spring Security OAuth2 Resource Servers validating asymmetric `RS256` JWTs via JWKS (`iss`, `aud`, and short-lived `exp` claims)
- [ ] Enforce Segregation of Duties (SoD) and defense-in-depth (`SecurityFilterChain` + `@PreAuthorize`) across `ROLE_PRICING_ADMIN`, `ROLE_ATTENDANT`, `ROLE_STATION_MANAGER`, and `ROLE_OPS_ADMIN`
- [ ] Prevent BOLA/IDOR vulnerabilities by validating branch ownership (`station_id` JWT claim) on pump and refueling operations, and lock down `/actuator/**` endpoints under a deny-by-default policy (exposing only `/actuator/health/liveness` and `/readiness` to orchestrators)
- [ ] Harden RabbitMQ with least-privilege service accounts (`pricing_publisher` write-only on exchange; `gas_station_consumer` read-only on queue) and propagate immutable audit claims (`triggeredBySub`, `correlationId`) without passing expiring JWTs in messages

### Phase 5: Data & AI Integration — Operational Copilot *(RAG, Agents & AI Governance Simulation)*
> Simulates an AI-assisted station copilot combining live transactional APIs with regulatory/operational knowledge bases, enforcing grounded responses, authorization boundaries, and prompt-injection defenses.

- [ ] Add `station-copilot-service` (Java 25 / Spring AI) and enable the `pgvector` extension on PostgreSQL 17 for unified relational and vector persistence
- [ ] Build document ingestion and semantic search pipeline (chunking, embeddings, HNSW cosine index, and metadata filtering) over ANP regulations, NR-20 safety procedures, FISPQ sheets, and internal policies (RAG)
- [ ] Implement typed agent tools (`@Tool`) integrating with `pricing-service` and `gas-station-api`, plus an MCP Server endpoint for standardized external AI consumption
- [ ] Enforce structured response citations (`citations[]`, `executedTools[]`) and automated functional evaluation suites (*golden dataset* testing groundedness, context recall, and hallucination resistance)
- [ ] Enforce Zero-Trust AI guardrails: propagate user JWTs (`station_id` and `ROLE_*` scopes) into tool calls, require Human-in-the-Loop (`PENDING_APPROVAL`) for price changes, and isolate untrusted RAG context (`<untrusted_document_context>`) against indirect prompt injection

---

## 🔄 Phases 2–4 Reference Architecture — Communication, Consistency & Application Reliability

This section serves as an implementation blueprint mapping distributed systems communication, data consistency, and production reliability practices across **Phases 2, 3, and 4**.

### 1. Communication & Consistency
- **Messaging, Events & Persistence (3.1):**
  - **RabbitMQ** with durable Topic Exchanges (`pricing.events`), bounded retry queues, and Dead Letter Queues (`pricing.events.dlq`).
  - **Transactional Outbox Pattern:** `pricing-service` persists domain updates (`fuel_prices`) and outbox records (`outbox_events`) in the same local ACID transaction; an asynchronous relay publishes pending events to RabbitMQ, avoiding dual-write inconsistencies.
  - **Local Read-Model Replication:** `gas-station-api` consumes `FuelPriceUpdatedEvent` messages into a local `replicated_fuel_prices` table inside `db_posto`.
- **Failures, Retries, Idempotency & Consistency (3.2):**
  - **HTTP Layer Idempotency:** `POST /api/v1/refuelings` accepts an `Idempotency-Key` request header persisted with a unique constraint and response snapshot so client/network retries never duplicate refueling transactions.
  - **Messaging Idempotency:** Event consumers record `eventId` in a `processed_events` table before applying updates to `replicated_fuel_prices`, making message redelivery (`at-least-once`) safe and idempotent.
  - **Synchronous Fault Handling:** Resilience4j Retry (with exponential backoff + jitter) and Circuit Breaker on `PricingClient` (`RestClient`).
- **System Integration & Operation Coordination (3.3):**
  - **Phase 2 (Synchronous Request/Response):** `gas-station-api` coordinates price lookups via `RestClient` against `pricing-service`.
  - **Phase 3 (Asynchronous Event Choreography):** Decouples forecourt operations from `pricing-service` availability by shifting to event-driven choreography via RabbitMQ.
- **Contract Evolution & Schema Migration (3.4):**
  - **Database Migrations:** Managed via **Flyway** (`src/main/resources/db/migration/V*__*.sql`) using the *expand-and-contract* pattern for zero-downtime schema changes across `db_posto` and `db_pricing`.
  - **REST & Event Contracts:** Documented with OpenAPI 3 (`springdoc-openapi`), non-breaking URI versioning (`/api/v1/prices` $\rightarrow$ `/api/v2/prices`), and explicit `schemaVersion` metadata inside RabbitMQ JSON payloads.

### 2. Application Reliability & Observability
- **Containers, Cloud & Serverless Environments (5.1):**
  - **Containers:** Multi-stage `Dockerfile`s (non-root JRE 25 runtime) and local orchestration via `compose.yaml`.
  - **Cloud / Kubernetes (`k8s/`):** Reference `Deployment`, `Service`, `ConfigMap`, and `HorizontalPodAutoscaler` (HPA) manifests wired to Spring Boot Actuator probes.
  - **Serverless Event Processing:** Lightweight event-driven function triggered by RabbitMQ refueling/pricing events to emit digital receipts or notify station managers of price threshold anomalies.
- **Logs, Metrics & Distributed Tracing (5.2):**
  - **Structured Logging:** Logback configured with JSON encoder (`logstash-logback-encoder`) automatically injecting `traceId`, `spanId`, `correlationId`, and `station_id`.
  - **Metrics:** Micrometer + `/actuator/prometheus` scraped by **Prometheus** and visualized in **Grafana** (JVM, HikariCP connection pools, RabbitMQ consumer lag, and refueling throughput).
  - **Distributed Tracing:** **OpenTelemetry** (`micrometer-tracing-bridge-otel`) exporting traces to **Jaeger / Grafana Tempo**, propagating W3C `traceparent` headers across `gas-station-api` $\rightarrow$ `pricing-service` HTTP calls and RabbitMQ message properties.
- **Latency, Availability, Capacity & Fault Tolerance (5.3):**
  - **Low-Latency Caching:** Redis cache-aside in `pricing-service` serving digital totem queries in sub-millisecond latency.
  - **Capacity & Load Testing:** Automated **k6** scripts benchmarking `p95`/`p99` latency and throughput (RPS) with and without Redis and under downstream latency injection.
- **Integration & Dependency Diagnostics (5.4):**
  - **Spring Boot Actuator Health Probes:** `/actuator/health/liveness` and `/actuator/health/readiness` backed by health indicators for PostgreSQL (`db_posto` / `db_pricing`), Redis, and RabbitMQ.
  - **Dependency Latency Breakdown:** OpenTelemetry spans isolating JDBC query duration, Redis cache hits/misses, and RabbitMQ publish/consume latency.
- **Service Continuity, Recovery & Graceful Degradation (5.5):**
  - **Zero-Downtime Outage Handling:** If `pricing-service` is stopped (`docker stop pricing-service`), `gas-station-api` degrades gracefully by reading from `replicated_fuel_prices` so forecourt pumps never stop registering refuelings.
  - **Self-Healing Recovery:** When `pricing-service` and RabbitMQ recover, queued Outbox/broker events replay automatically in order, and poisoned messages routed to the DLQ can be inspected and reprocessed safely.

---

## 🤖 Phase 5 Reference Architecture — Operational & Compliance Copilot (`station-copilot-service`)

To serve as an implementation blueprint for **Phase 5**, the platform evolves into an **Operational and Compliance Copilot** that assists forecourt attendants and station managers by crossing live transactional data (prices, pumps, and daily cash closing) with unstructured regulatory and operational documents (ANP regulations, NR-20 safety standards for flammables, FISPQ chemical safety sheets, and operational manuals).

### 1. Consumption & Exposure of Data Services and Language Models
- **LLM & Embedding Consumption:** `station-copilot-service` (Java 25 + Spring AI / LangChain4j) consumes an LLM (via Gemini/OpenAI API or local Ollama) for reasoning and synthesis, an embedding model for vectorization, and the internal REST microservices (`gas-station-api` and `pricing-service`) as structured data sources.
- **Service Exposure (REST SSE + MCP Server):**
  - Exposes a conversational streaming endpoint: `POST /api/v1/copilot/chat` (Server-Sent Events / SSE).
  - Exposes a standardized **MCP Server (Model Context Protocol)** publishing controlled resources and tools (current fuel prices, daily refueling summaries) for external AI clients.

### 2. Vector Representations, Semantic Search & RAG (`pgvector`)
- **Document Knowledge Base:** Automated ingestion of PDFs/Markdown covering ANP resolutions, tanker-truck unloading procedures, NR-20 rules, FISPQ sheets, and internal discount policies.
- **PostgreSQL `pgvector` Pipeline:**
  - Splits documents into chunks enriched with structured metadata (`document_type`, `effective_date`, `fuel_type`).
  - Persists embeddings in a `knowledge_embeddings` table inside PostgreSQL 17 indexed with **HNSW** (`vector_cosine_ops`)—avoiding operational overhead from an external vector database.
  - **Hybrid Retrieval:** Combines cosine-similarity semantic search with deterministic SQL metadata filters (e.g., filtering only currently effective regulations) before injecting context into the prompt.

### 3. Agents & Typed Tools (Function Calling / Tool Use)
Instead of a static chatbot, the service acts as a routing agent deciding whether to query the vector knowledge base (RAG), invoke typed domain tools (`@Tool`), or combine both:
- `getCurrentFuelPrices()`: Queries `pricing-service` to answer real-time pricing questions (e.g., *"What is the current price of additive gasoline?"*).
- `getStationDailySummary(stationId, date)`: Aggregates dispensed volume in liters and total revenue per fuel type from `gas-station-api`.
- `draftPriceUpdate(fuelTypeId, proposedPrice, justification)`: Creates a price adjustment proposal based on cost/margin analysis without mutating production prices directly.

### 4. Functional Evaluation of Responses & Sources (Groundedness & Evals)
- **Mandatory Source Traceability:** The `CopilotResponse` DTO enforces a structured contract returning `answer`, `citations[]` (document name, section, retrieved excerpt, and similarity score), and `executedTools[]` (which backend endpoints/tools were invoked).
- **Automated CI Evaluation Suite (*Golden Dataset*):** Automated test suite validating curated question/answer/source pairs against:
  - **Groundedness / Faithfulness:** Ensures the model never hallucinates liter volumes, prices, or ANP regulatory rules absent from the retrieved context or tool outputs.
  - **Context Recall:** Verifies that the expected regulatory/manual chunks consistently rank within the `top-k` vector search results.

### 5. Authorization Boundaries, Human Supervision & Prompt-Injection Defense
- **Zero-Trust Identity & Scope Propagation in Tools:** The agent never uses a privileged superuser account to call internal APIs; it propagates the authenticated caller's `Bearer` JWT (from Keycloak):
  - If an attendant (`ROLE_ATTENDANT`) asks for another branch's revenue, the tool call fails on `station_id` ownership validation (preventing BOLA/IDOR via natural language).
  - If a caller lacks `ROLE_PRICING_ADMIN`, write-capable pricing tools are omitted from the dynamic tool binding.
- **Human-in-the-Loop (HITL) Supervision:** Tools with financial side effects (such as `draftPriceUpdate`) never execute `PUT /api/v1/prices` directly; they persist a draft with `status = PENDING_APPROVAL` requiring explicit confirmation by an authorized human manager.
- **Indirect Prompt-Injection Defense:** Untrusted external documents retrieved via RAG and free-text fields (e.g., supplier notes) are wrapped in strict structural delimiters (`<untrusted_document_context>`) paired with system instructions enforcing that enclosed content be treated strictly as passive data—blocking embedded instructions attempting to exfiltrate data or trigger unauthorized tool calls.

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