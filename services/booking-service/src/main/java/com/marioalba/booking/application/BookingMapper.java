package com.marioalba.booking.application;

import com.marioalba.booking.adapters.persistence.entity.BookingEntity;
import com.marioalba.booking.api.dto.BookingResponse;
import com.marioalba.booking.api.dto.BookingStatusDto;
import com.marioalba.booking.api.dto.ServiceCode;
import java.time.Instant;

public class BookingMapper {
  private BookingMapper() {}

  public static BookingResponse toResponse(BookingEntity e) {
    return BookingResponse.builder()
        .bookingId(e.getBookingId())
        .shopId(e.getShopId())
        .barberId(e.getBarberId())
        .customerId(e.getCustomerId())
        .date(e.getBookingDate())
        .startTime(e.getStartTime())
        .endTime(e.getEndTime())
        .durationMinutes(e.getDurationMinutes())
        .serviceCode(ServiceCode.valueOf(e.getServiceCode()))
        .status(BookingStatusDto.valueOf(e.getStatus()))
        .createdAt(e.getCreatedAt())
        .updatedAt(e.getUpdatedAt())
        .build();
  }

  public static void applyNewStatus(BookingEntity e, BookingStatusDto status) {
    e.setStatus(status.name());
    e.setUpdatedAt(Instant.now());
  }
}
