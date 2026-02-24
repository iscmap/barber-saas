package com.marioalba.booking.application;

import com.marioalba.booking.api.dto.BookingStatusDto;

public final class BookingStateMachine {

  private BookingStateMachine() {}

  /**
   * Applies an availability decision to the current booking status. - PENDING -> CONFIRMED/REJECTED
   * - CONFIRMED and REJECTED are terminal - Duplicate decisions are idempotent (no change) -
   * Conflicting decisions after terminal state are invalid
   */
  public static BookingStatusDto applyAvailabilityDecision(
      BookingStatusDto current, AvailabilityDecision decision) {
    if (current == null) throw new IllegalArgumentException("current status is null");
    if (decision == null) throw new IllegalArgumentException("decision is null");

    return switch (current) {
      case PENDING ->
          switch (decision) {
            case RESERVED -> BookingStatusDto.CONFIRMED;
            case REJECTED -> BookingStatusDto.REJECTED;
          };

      case CONFIRMED ->
          switch (decision) {
            case RESERVED -> BookingStatusDto.CONFIRMED; // idempotent duplicate
            case REJECTED ->
                throw new InvalidBookingTransitionException(
                    "Cannot move CONFIRMED -> REJECTED (conflicting decision)");
          };

      case REJECTED ->
          switch (decision) {
            case REJECTED -> BookingStatusDto.REJECTED; // idempotent duplicate
            case RESERVED ->
                throw new InvalidBookingTransitionException(
                    "Cannot move REJECTED -> CONFIRMED (conflicting decision)");
          };
    };
  }
}
