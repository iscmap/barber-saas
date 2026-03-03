package com.marioalba.common.events.booking;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookingCreatedV1 {
  String bookingId;
  String barberId;
  String customerId;
  LocalDate date;
  LocalTime startTime;
  Integer durationMinutes;
  String serviceCode;
}
