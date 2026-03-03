package com.marioalba.booking.api.filter;

public class MissingHeaderException extends RuntimeException {
  public MissingHeaderException(String headerName) {
    super("Missing required header: " + headerName);
  }
}
