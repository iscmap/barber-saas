package com.marioalba.availability.events;

import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BookingCreatedV1 {
  String bookingId;
  String barberId;
  String customerId;
  LocalDate date;
  LocalTime startTime;
  Integer durationMinutes;
  String serviceCode;
}
