package com.marioalba.booking.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.marioalba.booking.api.dto.BookingStatusDto;
import org.junit.jupiter.api.Test;

public class BookingStateMachineTest {

  @Test
  void pending_to_confirmed_when_reserved() {
    BookingStatusDto next =
        BookingStateMachine.applyAvailabilityDecision(
            BookingStatusDto.PENDING, AvailabilityDecision.RESERVED);
    assertEquals(BookingStatusDto.CONFIRMED, next);
  }

  @Test
  void pending_to_rejected_when_rejected() {
    BookingStatusDto next =
        BookingStateMachine.applyAvailabilityDecision(
            BookingStatusDto.PENDING, AvailabilityDecision.REJECTED);
    assertEquals(BookingStatusDto.REJECTED, next);
  }

  @Test
  void confirmed_is_terminal_and_reserved_is_idempotent() {
    BookingStatusDto next =
        BookingStateMachine.applyAvailabilityDecision(
            BookingStatusDto.CONFIRMED, AvailabilityDecision.RESERVED);
    assertEquals(BookingStatusDto.CONFIRMED, next);
  }

  @Test
  void rejected_is_terminal_and_rejected_is_idempotent() {
    BookingStatusDto next =
        BookingStateMachine.applyAvailabilityDecision(
            BookingStatusDto.REJECTED, AvailabilityDecision.REJECTED);
    assertEquals(BookingStatusDto.REJECTED, next);
  }

  @Test
  void confirmed_cannot_become_rejected() {
    assertThrows(
        InvalidBookingTransitionException.class,
        () ->
            BookingStateMachine.applyAvailabilityDecision(
                BookingStatusDto.CONFIRMED, AvailabilityDecision.REJECTED));
  }

  @Test
  void rejected_cannot_become_confirmed() {
    assertThrows(
        InvalidBookingTransitionException.class,
        () ->
            BookingStateMachine.applyAvailabilityDecision(
                BookingStatusDto.REJECTED, AvailabilityDecision.RESERVED));
  }
}
