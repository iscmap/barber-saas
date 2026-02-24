package com.marioalba.booking.application;

public class InvalidBookingTransitionException extends RuntimeException {
  public InvalidBookingTransitionException(String message) {
    super(message);
  }
}
