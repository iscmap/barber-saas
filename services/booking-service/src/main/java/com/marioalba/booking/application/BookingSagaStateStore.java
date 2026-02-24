package com.marioalba.booking.application;

import com.marioalba.booking.api.dto.BookingStatusDto;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;

@Component
public class BookingSagaStateStore {
  private final ConcurrentHashMap<String, BookingSagaState> stateByBookingId =
      new ConcurrentHashMap<>();

  public void initPending(String bookingId) {
    Instant now = Instant.now();
    stateByBookingId.put(bookingId, new BookingSagaState(BookingStatusDto.PENDING, now, now));
  }

  public Optional<BookingSagaState> getState(String bookingId) {
    return Optional.ofNullable(stateByBookingId.get(bookingId));
  }

  public Optional<BookingStatusDto> getStatus(String bookingId) {
    return getState(bookingId).map(BookingSagaState::status);
  }

  public void setStatus(String bookingId, BookingStatusDto newStatus) {
    stateByBookingId.computeIfPresent(
        bookingId,
        (id, current) -> new BookingSagaState(newStatus, current.createdAt(), Instant.now()));
  }

  public Stream<Map.Entry<String, BookingSagaState>> streamAll() {
    return stateByBookingId.entrySet().stream();
  }
}
