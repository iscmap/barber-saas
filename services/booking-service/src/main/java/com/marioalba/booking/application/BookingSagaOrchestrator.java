package com.marioalba.booking.application;

import com.marioalba.booking.api.dto.BookingStatusDto;
import org.springframework.stereotype.Component;

@Component
public class BookingSagaOrchestrator {
  private final BookingSagaStateStore store;

  public BookingSagaOrchestrator(BookingSagaStateStore store) {
    this.store = store;
  }

  /**
   * Apply an AvailabilityDecision to a booking's current status. Safe against duplicates and
   * conflicting decisions (logs + ignores).
   */
  public void handleAvailabilityDecision(String bookingId, AvailabilityDecision decision) {
    BookingStatusDto current =
        store
            .getStatus(bookingId)
            .orElseThrow(
                () -> new IllegalStateException("Booking not found in saga store: " + bookingId));

    try {
      BookingStatusDto next = BookingStateMachine.applyAvailabilityDecision(current, decision);
      store.setStatus(bookingId, next);
      // idempotent no-op is fine (next == current)
      if (next != current) {
        store.setStatus(bookingId, next);
        System.out.println("Booking " + bookingId + " transitioned " + current + " -> " + next);
      } else {
        System.out.println("Booking " + bookingId + " idempotent decision, stays " + current);
      }
    } catch (InvalidBookingTransitionException e) {
      System.err.println("Invalid transition for booking " + bookingId + ": " + e.getMessage());
    }
  }
}
