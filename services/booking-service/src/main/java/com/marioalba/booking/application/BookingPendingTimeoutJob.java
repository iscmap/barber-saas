package com.marioalba.booking.application;

import com.marioalba.booking.adapters.persistence.BookingJpaRepository;
import com.marioalba.booking.adapters.persistence.entity.BookingEntity;
import com.marioalba.booking.api.dto.BookingStatusDto;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@EnableScheduling
@Component
public class BookingPendingTimeoutJob {

  private final BookingJpaRepository repo;
  private final long pendingTimeoutSeconds;

  public BookingPendingTimeoutJob(
      BookingJpaRepository repo,
      @Value("${booking.saga.pending-timeout-seconds:30}") long pendingTimeoutSeconds) {
    this.repo = repo;
    this.pendingTimeoutSeconds = pendingTimeoutSeconds;
  }

  @Scheduled(fixedDelayString = "${booking.saga.timeout-scan-ms:1000}")
  public void scanAndTimeout() {
    Instant cutoff = Instant.now().minus(Duration.ofSeconds(pendingTimeoutSeconds));

    List<BookingEntity> stale =
        repo.findByStatusAndCreatedAtBefore(BookingStatusDto.PENDING.name(), cutoff);
    for (BookingEntity e : stale) {
      e.setStatus(BookingStatusDto.REJECTED.name());
      e.setUpdatedAt(Instant.now());
      repo.save(e);
      System.out.println("DB Booking " + e.getBookingId() + " timed out -> REJECTED");
    }
  }
}
