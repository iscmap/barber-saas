package com.marioalba.booking.api.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RequiredHeadersInterceptor implements HandlerInterceptor {

  @Override
  public boolean preHandle(
      HttpServletRequest request, HttpServletResponse response, Object handler) {

    String shopId = request.getHeader(BookingRequestContextFilter.HDR_SHOP);
    if (shopId == null || shopId.isBlank()) {
      throw new MissingHeaderException(BookingRequestContextFilter.HDR_SHOP);
    }

    // Idempotency-Key required for POST /bookings (v1)
    if (HttpMethod.POST.matches(request.getMethod())
        && request.getRequestURI().equals("/bookings")) {

      String idem = request.getHeader("Idempotency-Key");
      if (idem == null || idem.isBlank()) {
        throw new MissingHeaderException("Idempotency-Key");
      }
    }

    return true;
  }
}
