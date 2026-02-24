package com.marioalba.booking.application;

public enum AvailabilityDecision {
  RESERVED,
  REJECTED;

  public static AvailabilityDecision fromWireValue(String value) {
    if (value == null) throw new IllegalArgumentException("decision is null");
    return AvailabilityDecision.valueOf(value.trim().toUpperCase());
  }
}
