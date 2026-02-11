package com.marioalba.booking.api;

import com.marioalba.booking.api.dto.BookingRequest;
import com.marioalba.booking.api.dto.BookingResponse;
import com.marioalba.booking.api.dto.BookingStatusDto;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BookingController {

  @PostMapping("/bookings")
  public ResponseEntity<?> createBooking(
      @RequestHeader("Idempotency-Key") String idempotencyKey,
      @Valid @RequestBody BookingRequest request) {
    BookingResponse response =
        BookingResponse.builder()
            .bookingId("bkg_" + UUID.randomUUID())
            .shopId(request.getShopId())
            .barberId(request.getBarberId())
            .customerId(request.getCustomerId())
            .date(request.getDate())
            .startTime(request.getStartTime())
            .endTime(request.getStartTime().plusMinutes(request.getDurationMinutes()))
            .durationMinutes(request.getDurationMinutes())
            .serviceCode(request.getServiceCode())
            .status(BookingStatusDto.PENDING)
            .createdAt(Instant.now())
            .build();

    return ResponseEntity.created(URI.create("/bookings/" + response.getBookingId()))
        .body(response);
  }

  @GetMapping("/bookings/{bookingId}")
  public ResponseEntity<BookingResponse> getBooking(@PathVariable String bookingId) {
    return ResponseEntity.notFound().build();
  }
}
