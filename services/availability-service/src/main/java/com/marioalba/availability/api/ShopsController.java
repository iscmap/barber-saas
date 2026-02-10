package com.marioalba.availability.api;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/shops")
public class ShopsController {

  @PutMapping("/{shopId}/schedule/{date}")
  ResponseEntity<?> shopsSchedule(
      @PathVariable @NotBlank String shopId,
      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    return ResponseEntity.ok().build();
  }

  @GetMapping("/{shopId}/availability/{date}")
  public ResponseEntity<?> getAvailability(
      @PathVariable @NotBlank String shopId,
      @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    return ResponseEntity.ok().build();
  }
}
