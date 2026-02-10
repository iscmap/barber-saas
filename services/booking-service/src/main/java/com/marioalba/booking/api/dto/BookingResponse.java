package com.marioalba.booking.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Value;

@Value
public class BookingResponse {
  String bookingId;
  String shopId;
  String barberId;
  String customerId;

  @JsonFormat(pattern = "yyyy-MM-dd")
  LocalDate date;

  @JsonFormat(pattern = "HH:mm")
  LocalTime startTime;

  Integer durationMinutes;

  BookingStatus status;

  Instant createdAt;
}
