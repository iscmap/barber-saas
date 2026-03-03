package com.marioalba.common.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventEnvelope<T> {
  String eventId;
  String eventType; // e.g. "BookingCreated"
  String version; // e.g. "v1"
  Instant occurredAt;

  String correlationId;
  String shopId;

  T payload;
}
