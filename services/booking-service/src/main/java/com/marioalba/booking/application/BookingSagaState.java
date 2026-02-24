package com.marioalba.booking.application;

import com.marioalba.booking.api.dto.BookingStatusDto;
import java.time.Instant;

public record BookingSagaState(BookingStatusDto status, Instant createdAt, Instant updatedAt) {}
