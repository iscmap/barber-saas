package com.marioalba.booking.api;

import com.marioalba.booking.api.dto.BookingRequest;
import com.marioalba.booking.api.dto.BookingResponse;
import com.marioalba.booking.api.dto.BookingStatusDto;
import com.marioalba.booking.events.BookingCreatedV1;
import com.marioalba.booking.events.EventEnvelope;
import com.marioalba.booking.messaging.BookingEventPublisher;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BookingController {

  private final BookingEventPublisher eventPublisher;

  public BookingController(BookingEventPublisher eventPublisher) {
    this.eventPublisher = eventPublisher;
  }

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

    // EVENT PAYLOAD
    BookingCreatedV1 payload =
        BookingCreatedV1.builder()
            .bookingId(response.getBookingId())
            .barberId(response.getBarberId())
            .customerId(response.getCustomerId())
            .date(response.getDate())
            .startTime(response.getStartTime())
            .durationMinutes(response.getDurationMinutes())
            .serviceCode(response.getServiceCode().name())
            .build();

    EventEnvelope<BookingCreatedV1> envelope =
        EventEnvelope.<BookingCreatedV1>builder()
            .eventId(UUID.randomUUID().toString())
            .eventType("BookingCreated")
            .version("v1")
            .occurredAt(Instant.now())
            .correlationId(idempotencyKey)
            .shopId(response.getShopId())
            .payload(payload)
            .build();

    eventPublisher.publish(envelope);

    return ResponseEntity.created(URI.create("/bookings/" + response.getBookingId()))
        .body(response);
  }

  @GetMapping("/bookings/{bookingId}")
  public ResponseEntity<BookingResponse> getBooking(@PathVariable String bookingId) {
    return ResponseEntity.notFound().build();
  }
}
