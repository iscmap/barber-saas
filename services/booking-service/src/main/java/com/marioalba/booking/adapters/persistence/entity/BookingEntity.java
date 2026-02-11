package com.marioalba.booking.adapters.persistence.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
    name = "bookings",
    indexes = {
      @Index(
          name = "idx_bookings_shop_barber_date_start",
          columnList = "shopId, barberId, bookingDate, startTime")
    })
@Getter
@Setter
public class BookingEntity {
  @Id
  @Column(name = "booking_id", length = 64)
  private String bookingId;

  @Column(name = "shop_id", nullable = false, length = 64)
  private String shopId;

  @Column(name = "barber_id", nullable = false, length = 64)
  private String barberId;

  @Column(name = "customer_id", nullable = false, length = 64)
  private String customerId;

  @Column(name = "booking_date", nullable = false)
  private LocalDate bookingDate;

  @Column(name = "start_time", nullable = false)
  private LocalTime startTime;

  @Column(name = "end_time", nullable = false)
  private LocalTime endTime;

  @Column(name = "duration_minutes", nullable = false)
  private Integer durationMinutes;

  @Column(name = "service_code", nullable = false, length = 64)
  private String serviceCode;

  @Column(name = "status", nullable = false, length = 32)
  private String status;

  @Column(name = "created_at", nullable = false)
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;
}
