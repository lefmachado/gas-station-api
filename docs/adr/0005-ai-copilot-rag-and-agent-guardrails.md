# 5. Adopt PostgreSQL `pgvector` Hybrid RAG, Typed Agent Tools, and Zero-Trust AI Guardrails

- **Status:** Proposed (Phase 5 Blueprint)
- **Date:** 2026-11-20

## Context
Station attendants, managers, and compliance auditors need to cross live transactional metrics (fuel prices, pump status, daily revenue) with unstructured regulatory and operational documents (ANP resolutions, NR-20 safety procedures, FISPQ sheets, and operational manuals). However, introducing LLMs into a financial and safety-critical domain introduces risks of hallucinated rules/numbers, cross-branch data leakage (BOLA/IDOR via natural language), unauthorized financial mutations, and indirect prompt injection from external documents.

## Decision
Introduce `station-copilot-service` (Java 25 + Spring AI) in Phase 5 governed by five architectural pillars:
1. **Unified Vector & Relational Store (`pgvector`):** Reuse PostgreSQL 17 with the `pgvector` extension (`knowledge_embeddings` table + HNSW `vector_cosine_ops` index) and combine cosine similarity with SQL metadata filters (`document_type`, `effective_date`, `fuel_type`) for hybrid RAG.
2. **Dual Exposure (REST SSE + MCP Server):** Expose `POST /api/v1/copilot/chat` (Server-Sent Events) for interactive chat and a standardized Model Context Protocol (MCP) Server for controlled external AI client integration.
3. **Typed Domain Tools (`@Tool`):** Expose deterministic read tools (`getCurrentFuelPrices`, `getStationDailySummary`) and a non-destructive proposal tool (`draftPriceUpdate`) calling `pricing-service` and `gas-station-api`.
4. **Verifiable Groundedness & CI Evals:** Require `CopilotResponse` to return structured `citations[]` and `executedTools[]`, validated in CI against a *golden dataset* measuring faithfulness/groundedness and context recall.
5. **Zero-Trust AI Guardrails:**
   - Propagate the caller's Keycloak `Bearer` JWT into every tool invocation (enforcing `station_id` ownership and `ROLE_*` scopes; omit write tools from bindings when unauthorized).
   - Enforce **Human-in-the-Loop (HITL)** by recording price update requests as `PENDING_APPROVAL` drafts rather than executing `PUT /api/v1/prices` directly.
   - Isolate retrieved RAG chunks and free-text inputs inside `<untrusted_document_context>` delimiters with explicit system prompts treating enclosed text strictly as passive data.

## Consequences
- Eliminates the need to operate a separate vector database by leveraging PostgreSQL 17 `pgvector`.
- Guarantees that natural-language queries cannot bypass existing OAuth2/Keycloak RBAC, Segregation of Duties, or branch-level (`station_id`) authorization.
- Prevents financial side effects or indirect prompt-injection attacks from autonomously altering pump prices, while providing auditable citations for regulatory compliance.
