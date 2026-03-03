package com.marioalba.booking.api;

import com.marioalba.booking.api.dto.BookingRequest;
import com.marioalba.booking.api.dto.BookingResponse;
import com.marioalba.booking.application.BookingMapper;
import com.marioalba.booking.application.BookingPersistenceService;
import com.marioalba.booking.messaging.BookingEventPublisher;
import com.marioalba.common.events.EventEnvelope;
import com.marioalba.common.events.booking.BookingCreatedV1;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class BookingController {

  private final BookingEventPublisher eventPublisher;
  private final BookingPersistenceService bookingService;

  public BookingController(
      BookingEventPublisher eventPublisher, BookingPersistenceService bookingService) {
    this.eventPublisher = eventPublisher;
    this.bookingService = bookingService;
  }

  @PostMapping("/bookings")
  public ResponseEntity<?> createBooking(
      @RequestHeader("Idempotency-Key") String idempotencyKey,
      @Valid @RequestBody BookingRequest request) {

    String bookingId = "bkg_" + UUID.randomUUID();

    // Persist PENDING booking
    var entity = bookingService.createPending(bookingId, request);
    var response = BookingMapper.toResponse(entity);

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
    return bookingService
        .findById(bookingId)
        .map(BookingMapper::toResponse)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  /**
   * @GetMapping("/bookings/{bookingId}/status") public ResponseEntity<?> getStatus(@PathVariable
   * String bookingId) { BookingResponse response = bookingService .findById(bookingId)
   * .map(BookingMapper::toResponse) .map(ResponseEntity::ok) .orElseGet(() ->
   * ResponseEntity.notFound().build());
   *
   * <p>}*
   */
}
