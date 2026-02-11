package com.marioalba.booking.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingResponse {
  String bookingId;
  String shopId;
  String barberId;
  String customerId;

  @JsonFormat(pattern = "yyyy-MM-dd")
  LocalDate date;

  @JsonFormat(pattern = "HH:mm")
  LocalTime startTime;

  @JsonFormat(pattern = "HH:mm")
  LocalTime endTime;

  Integer durationMinutes;

  ServiceCode serviceCode;

  BookingStatusDto status;

  Instant createdAt;
  Instant updatedAt;
}
