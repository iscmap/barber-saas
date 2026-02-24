package com.marioalba.booking.application;

import com.marioalba.booking.adapters.persistence.BookingJpaRepository;
import com.marioalba.booking.adapters.persistence.entity.BookingEntity;
import com.marioalba.booking.api.dto.BookingRequest;
import com.marioalba.booking.api.dto.BookingStatusDto;
import java.time.Instant;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BookingPersistenceService {
  private final BookingJpaRepository repo;

  public BookingPersistenceService(BookingJpaRepository repo) {
    this.repo = repo;
  }

  @Transactional
  public BookingEntity createPending(String bookingId, BookingRequest req) {
    BookingEntity e = new BookingEntity();
    e.setBookingId(bookingId);
    e.setShopId(req.getShopId());
    e.setBarberId(req.getBarberId());
    e.setCustomerId(req.getCustomerId());
    e.setBookingDate(req.getDate());
    e.setStartTime(req.getStartTime());
    e.setEndTime(req.getStartTime().plusMinutes(req.getDurationMinutes()));
    e.setDurationMinutes(req.getDurationMinutes());
    e.setServiceCode(req.getServiceCode().name());
    e.setStatus(BookingStatusDto.PENDING.name());
    e.setCreatedAt(Instant.now());
    e.setUpdatedAt(null);
    return repo.save(e);
  }

  public Optional<BookingEntity> findById(String bookingId) {
    return repo.findById(bookingId);
  }

  public void applyAvailabilityDecision(String bookingId, AvailabilityDecision decision) {
    BookingEntity e =
        repo.findById(bookingId)
            .orElseThrow(() -> new IllegalStateException("Booking not found: " + bookingId));

    BookingStatusDto current = BookingStatusDto.valueOf(e.getStatus());

    try {
      BookingStatusDto next = BookingStateMachine.applyAvailabilityDecision(current, decision);
      if (next != current) {
        BookingMapper.applyNewStatus(e, next);
        repo.save(e);
        System.out.println("DB Booking " + bookingId + " transitioned " + current + " -> " + next);
      } else {
        System.out.println("DB Booking " + bookingId + " idempotent decision, stays " + current);
      }
    } catch (InvalidBookingTransitionException ex) {
      System.err.println("Invalid transition for booking " + bookingId + ": " + ex.getMessage());
    }
  }
}
