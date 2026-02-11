package com.marioalba.availability.domain;

import java.time.LocalDate;

public final class Keys {

  private Keys() {}

  // shop_daily_barbers + slot_locks uses this PK
  public static String dayPk(String shopId, LocalDate date) {
    return "SHOP#" + shopId + "#DATE#" + date;
  }

  public static String barberSk(String barberId) {
    return "BARBER#" + barberId;
  }

  // slot lock SK: SLOT#HH:mm
  public static String slotSk(String startTime) {
    return "SLOT#" + startTime;
  }

  // slot lock PK includes barber for concurrency isolation
  public static String slotLockPk(String shopId, LocalDate date, String barberId) {
    return dayPk(shopId, date) + "#BARBER#" + barberId;
  }
}
