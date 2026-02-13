package com.marioalba.booking.events;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AvailabilityDecisionV1 {
  String bookingId;
  String decision; // "RESERVED" or "REJECTED"
  String reason; // optional
}
