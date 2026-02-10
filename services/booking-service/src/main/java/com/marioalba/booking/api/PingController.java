package com.marioalba.booking.api;

import com.marioalba.booking.api.dto.PingResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PingController {

  @GetMapping("/ping")
  public PingResponse ping() {
    return new PingResponse("booking-service: pong");
  }
}
