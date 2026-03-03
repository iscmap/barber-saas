# Non-Functional Requirements (NFR) — Barber SaaS (v1)

## Scope

Applies to all public HTTP APIs and asynchronous event processing.

## Latency (HTTP)

- P50: <= 150 ms
- P95: <= 500 ms
- P99: <= 1000 ms
  Notes:
- POST /bookings returns immediately with PENDING (async consistency)

## Throughput

- Target: 20 RPS per shop (burst 100 RPS for 10s)
- System-wide dev target: 200 RPS total

## Availability / Reliability

- HTTP availability: 99.9% monthly (dev target)
- Message processing: at-least-once delivery
- Consumers must be idempotent:
    - booking-service: decision events idempotent by bookingId + inbox (future step)
    - availability-service: reservations idempotent by bookingId (future step)

## Consistency

- Eventual consistency:
    - Booking: PENDING -> CONFIRMED/REJECTED via AvailabilityDecided event
- Timeout:
    - Booking PENDING auto-rejected after configured timeout

## Multi-tenancy

- Every request must include X-Shop-Id header (tenant scope)
- Every event must contain shopId field

## Correlation / Tracing

- Every request must include X-Correlation-Id
- If missing, server generates one and returns it
- Must be logged in all services (MDC)

## Idempotency

- POST endpoints require Idempotency-Key header
- Retries with same key must not create duplicates (Step 3)

## Error Model

- All errors are application/problem+json
- Response includes correlationId
- Validation errors are 400 with field-level errors

## Async messaging

- SQS queues have DLQ with maxReceiveCount=5
- Consumers:
    - retry on transient failures
    - send poison messages to DLQ
- Never crash-loop (must delete/ignore malformed messages in dev)

## Security (v1 baseline)

- No auth yet, but design is ready for JWT later:
    - tenant isolation by X-Shop-Id for now