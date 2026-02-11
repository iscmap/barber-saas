package com.marioalba.booking.api.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Value;

@Value
public class BookingRequest {

  @NotBlank private String shopId;

  @NotBlank private String barberId;

  @NotBlank private String customerId;

  @NotBlank
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate date;

  @NotBlank
  @JsonFormat(pattern = "HH:mm")
  private LocalTime startTime;

  @NotBlank
  @Min(1)
  private Integer durationMinutes;

  @NotBlank private ServiceCode serviceCode;
}
