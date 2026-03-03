package com.marioalba.common.events.availability;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class AvailabilityDecidedV1 {
  String bookingId;
  String decision; // "RESERVED" or "REJECTED"
  String reason; // optional
}
