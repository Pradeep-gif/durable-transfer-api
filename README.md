# Transfer Orchestrator

A compact Java backend project for a ten-minute technical walkthrough. It accepts transfer requests, stores them in PostgreSQL, prevents duplicate submissions with an idempotency key, and starts a Temporal workflow for asynchronous processing.

This is a portfolio demonstration inspired by common financial-service backend problems. It is not affiliated with SoFi and does not move real money or connect to a payment provider.

## Stack

Java 21, Spring Boot, REST, Bean Validation, PostgreSQL, Spring Data JPA, Temporal, Mockito, JUnit 5, Docker Compose, Kubernetes, GitHub Actions, and Spring Boot Actuator.

## Run locally

Requirements: Docker Compose, Java 21, and Maven 3.9+.

```bash
mvn test
docker compose up --build
```

API: http://localhost:8080. Temporal UI: http://localhost:8233. Health: http://localhost:8080/actuator/health.

## Try it

```bash
curl -i -X POST http://localhost:8080/api/transfers \
  -H 'Content-Type: application/json' \
  -H 'Idempotency-Key: demo-request-001' \
  -d '{"sourceAccountId":"acct-100","destinationAccountId":"acct-200","amount":25.00,"currency":"USD"}'
```

The API returns `202 Accepted` with a transfer ID and `PENDING` status. Query it with `GET /api/transfers/{id}`. Repeating the idempotency key returns the existing transfer instead of starting another workflow.

## Ten-minute walkthrough

1. **API contract (2 min):** `TransferController` validates requests and returns a stable response.
2. **Correctness (2 min):** `TransferService` rejects same-account transfers; a PostgreSQL unique constraint protects idempotency keys.
3. **Async work (2 min):** a Temporal workflow runs an activity that advances the transfer lifecycle. Workflow IDs derive from transfer IDs.
4. **Testing (2 min):** Mockito tests cover creation, repeat requests, and invalid account pairs.
5. **Operations (2 min):** Compose runs PostgreSQL and Temporal; Actuator exposes health and metrics; CI runs Maven verification; Kubernetes manifests include probes, resource limits, and secret references.

## Production design discussion

The activity currently marks a transfer complete as a demo. Production work would call a provider and post balanced ledger entries. It would also use a transactional outbox so database commit and workflow scheduling cannot diverge, check that a repeated idempotency key has the same request payload, add authorization and account ownership checks, and use managed secrets, migrations, structured logs, and provider-specific retry policies. The local defaults and `ddl-auto: update` are for demonstration only.
