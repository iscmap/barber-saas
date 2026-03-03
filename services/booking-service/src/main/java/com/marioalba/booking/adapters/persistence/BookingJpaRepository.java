package com.marioalba.booking.adapters.persistence;

import com.marioalba.booking.adapters.persistence.entity.BookingEntity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingJpaRepository extends JpaRepository<BookingEntity, String> {

  List<BookingEntity> findByStatusAndCreatedAtBefore(String status, Instant createdAtCutoff);

  Optional<BookingEntity> findByBookingIdAndShopId(String bookingId, String shopId);
}
