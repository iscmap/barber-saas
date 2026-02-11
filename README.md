# \# Multi-Tenant Barber Booking SaaS (Backend)

#  

# A multi-tenant (multi-barbershop) barber booking platform built with:

# \- Java (toolchain 21, Java 11 coding style)

# \- Spring Boot 3.2.x

# \- Postgres (bookings)

# \- DynamoDB (availability/reservations) via LocalStack

# \- SQS/SNS + DLQ via LocalStack

# \- Docker Compose for local infra

#  

# \## Quick start (later)

1) Which service owns the Booking lifecycle?

Answer: booking-service owns it.
It is the system of record for Booking and its state transitions: PENDING → CONFIRMED/REJECTED.

Why: bookings require transactional integrity, auditability, and idempotency storage → best fit for Postgres/RDS.

2) Which service owns schedules and availability?

Answer: availability-service owns:

daily barber schedule per shop/day

availability view (slots)

reservation locking (prevent double booking)

Why: access patterns are “by shop + date + barber”, high read/write, and we need conditional writes → great fit for
DynamoDB.

3) Why not put everything in one DB / one service?

Answer: Because availability has different scaling + consistency needs than bookings.

Bookings: transactional, relational, reporting → Postgres

Availability: fast lookups/locks by key, high concurrency → DynamoDB conditional updates

Also, splitting reduces coupling and lets each service scale independently.

4) How do you prevent double booking?

Answer: The availability-service is the “lock authority”.
It uses DynamoDB conditional writes (or conditional updates) to ensure only one reservation can claim a slot.

booking-service never “assumes” availability; it waits for the reservation outcome event.

5) Why async (events) instead of synchronous call from booking → availability?

Answer: Async improves resilience and decoupling:

booking-service can accept a request and respond fast (PENDING)

availability-service can process independently

we avoid cascading failures if availability is slow/down

we get at-least-once delivery with retry + DLQ

This is standard microservices eventual consistency.

6) What consistency model are you using?

Answer: Eventual consistency with a simple saga:

Booking starts PENDING

Availability decides RESERVED or REJECTED

Booking becomes CONFIRMED or REJECTED

Client sees PENDING briefly; consistent final state arrives after events.

7) Where do you enforce multi-tenancy isolation?

Answer: Everywhere via shopId:

Postgres booking rows include shopId

DynamoDB partition keys include shopId (e.g., shopId#date)

Events include shopId

APIs are scoped by shopId (path or payload)

This prevents data leakage across tenants.

8) Where does idempotency live?

Answer: Two places:

booking-service: request idempotency via Idempotency-Key stored in Postgres (Step 3)

availability-service: reservation idempotency by bookingId (Dynamo record) so reprocessing the same event doesn’t double
reserve

Why: at-least-once delivery means duplicates happen; idempotency is mandatory.

9) What happens if availability-service is down?

Answer: booking-service still returns a bookingId with PENDING.
The event sits in SQS until availability-service recovers (or goes to DLQ after retries).

You monitor DLQ depth and processing lag (CloudWatch later).

10) What happens if the reservation fails after booking is created?

Answer: booking transitions to REJECTED on AvailabilityRejected event.
Optionally you can notify the client or allow them to retry another slot.

No rollback across services; it’s a saga outcome.

11) What data does each service store?

Answer (high level):

booking-service (Postgres): bookings, idempotency keys, outbox, inbox (for consumer dedupe)

availability-service (Dynamo): daily schedules per shop/day, reservations per bookingId (and optionally precomputed
slots)

Each service owns its data; no shared DB.

12) How do you debug a single request across services?

Answer: Correlation IDs:

X-Correlation-Id propagated across HTTP and events

logged in both services

searchable in CloudWatch logs in AWS (Step 8)

# TBD

