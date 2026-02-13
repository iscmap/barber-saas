package com.marioalba.booking.events;

import java.time.Instant;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EventEnvelope<T> {
  String eventId;
  String eventType; // e.g. "BookingCreated"
  String version; // e.g. "v1"
  Instant occurredAt;

  String correlationId;
  String shopId;

  T payload;
}
