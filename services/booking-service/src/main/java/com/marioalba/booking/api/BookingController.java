package com.marioalba.booking.api;

import com.marioalba.booking.api.dto.BookingRequest;
import com.marioalba.booking.api.dto.BookingResponse;
import com.marioalba.booking.api.dto.BookingStatus;
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
        new BookingResponse(
            "bkg_" + UUID.randomUUID(),
            request.getShopId(),
            request.getBarberId(),
            request.getCustomerId(),
            request.getDate(),
            request.getStartTime(),
            request.getDurationMinutes(),
            BookingStatus.PENDING,
            Instant.now());

    return ResponseEntity.created(URI.create("/bookings/" + response.getBookingId()))
        .body(response);
  }

  @GetMapping("/bookings/{bookingId}")
  public ResponseEntity<BookingResponse> getBooking(@PathVariable String bookingId) {
    return ResponseEntity.notFound().build();
  }
}
