package com.marioalba.booking.adapters.persistence;

import com.marioalba.booking.adapters.persistence.entity.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingJpaRepository extends JpaRepository<BookingEntity, String> {}
