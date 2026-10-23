# Transaction API and Workflow Demo

A compact Java backend project that demonstrates an idempotent transaction API backed by PostgreSQL and Temporal. It is a portfolio prototype for discussing transaction correctness, durable orchestration, API contracts, automated testing, and service operations. It does not move real money or connect to a payment provider.

## Stack

- Java 21 and Spring Boot REST APIs with request validation
- PostgreSQL and Spring Data JPA
- Temporal workflow orchestration
- JUnit 5 and Mockito
- Docker Compose, Kubernetes manifests, GitHub Actions CI
- Spring Boot Actuator health and metrics endpoints

## Run locally

Requirements: Docker Compose, Java 21, and Maven 3.9+.

```bash
mvn test
docker compose up --build
```

The API listens on `http://localhost:8080`, Temporal UI on `http://localhost:8233`, and health checks on `http://localhost:8080/actuator/health`.

## Try it

```bash
curl -i -X POST http://localhost:8080/api/transfers \
  -H 'Content-Type: application/json' \
  -H 'Idempotency-Key: demo-request-001' \
  -d '{"sourceAccountId":"acct-100","destinationAccountId":"acct-200","amount":25.00,"currency":"USD"}'
```

The API returns `202 Accepted` with a transfer ID and `PENDING` status. Query it with `GET /api/transfers/{id}`. Repeating the same key and same request returns the existing transfer. Reusing that key with a different request returns `409 Conflict`.

## Ten-minute walkthrough

1. **API and data model (2 min):** inspect the validated request/response records and the PostgreSQL entity.
2. **Correctness (2 min):** trace the idempotency-key uniqueness constraint and the `409` behavior for key reuse with changed request data.
3. **Durable work (2 min):** follow the Temporal workflow and activity; workflow IDs derive from the persisted transfer ID.
4. **Tests (2 min):** run `mvn test`; tests cover creation, safe retries, changed-payload conflicts, and invalid same-account transfers.
5. **Operations (2 min):** review health/metrics endpoints, CI, container setup, Kubernetes probes, and the production considerations below.

## Production considerations

This demo marks a transfer complete through a local activity. A real money movement system would need a transactional outbox so database writes and workflow scheduling cannot diverge, provider integration with carefully scoped retries, ledger posting and reconciliation, authorization, schema migrations, managed secrets, structured logs, SLOs, and load/chaos testing. Local configuration uses Hibernate schema updates and development credentials; do not deploy it as-is.
