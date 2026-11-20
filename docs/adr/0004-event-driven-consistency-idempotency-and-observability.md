# 4. Event-Driven Consistency, Dual-Layer Idempotency, Flyway Migrations, and Full-Stack Observability

- **Status:** Proposed (Phases 2–4 Blueprint)
- **Date:** 2026-11-20

## Context
As `gas-station-api` evolves into a distributed system with an independent `pricing-service`, Redis cache, and RabbitMQ broker, several production challenges arise:
1. **Consistency & Availability:** Synchronous HTTP calls to `pricing-service` during refueling create a single point of failure if `pricing-service` goes down, while publishing events directly to RabbitMQ risks dual-write inconsistencies.
2. **Duplicate Operations:** Network timeouts on `POST /api/v1/refuelings` or `at-least-once` message redelivery in RabbitMQ can generate duplicate refueling records or out-of-order price updates.
3. **Schema Drift:** Relying on Hibernate `ddl-auto` across microservices risks unversioned, destructive schema changes.
4. **Blind Spots in Distributed Diagnostics:** Investigating latency spikes or integration failures across HTTP, Redis, PostgreSQL, and RabbitMQ requires unified health probes, metrics, structured logs, and end-to-end distributed traces.

## Decision
1. **Transactional Outbox & Local Read-Model Replication:** `pricing-service` writes price updates and `outbox_events` atomically in `db_pricing`, publishing to a durable RabbitMQ Topic Exchange with DLQ support. `gas-station-api` replicates prices into a local `replicated_fuel_prices` table so refueling registration continues with zero downtime even when `pricing-service` is offline.
2. **Dual-Layer Idempotency:**
   - **HTTP Layer:** Support an `Idempotency-Key` header on `POST /api/v1/refuelings` backed by a persisted idempotency table to return the original response on client retries.
   - **Messaging Layer:** Deduplicate incoming RabbitMQ messages via a `processed_events` table keyed by `eventId`.
3. **Contract & Schema Evolution:** Manage all DDL via **Flyway** (`src/main/resources/db/migration`) using expand-and-contract migrations, paired with OpenAPI 3 contracts, URI versioning (`/api/v2/prices`), and `schemaVersion` envelopes on events.
4. **Full-Stack Observability & Dependency Diagnostics:**
   - Expose **Spring Boot Actuator** `/actuator/health/liveness` and `/actuator/health/readiness` probes with Health Indicators for PostgreSQL, Redis, and RabbitMQ (wired to Docker Compose and Kubernetes `k8s/` manifests).
   - Emit structured JSON logs (Logback), scrape Micrometer metrics with **Prometheus + Grafana**, and propagate W3C **OpenTelemetry** (`traceId`, `spanId`) across REST calls and RabbitMQ headers into **Jaeger / Grafana Tempo**.

## Consequences
- Forecourt refueling operations achieve high availability and graceful degradation during `pricing-service` outages.
- Eliminates duplicate refueling charges and dual-write race conditions across HTTP and asynchronous messaging.
- Provides deterministic database migrations and full end-to-end observability (`traceId` correlation, `p95`/`p99` latency visibility, and dependency health checks).
