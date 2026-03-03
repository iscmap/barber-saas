package com.marioalba.booking.api.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class BookingRequestContextFilter extends OncePerRequestFilter {
  public static final String HDR_CORRELATION = "X-Correlation-Id";
  public static final String HDR_SHOP = "X-Shop-Id";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    String correlationId = request.getHeader(HDR_CORRELATION);
    if (correlationId == null || correlationId.isBlank()) {
      correlationId = "corr_" + UUID.randomUUID();
    }

    String shopId = request.getHeader(HDR_SHOP); // can be required later by interceptor

    MDC.put("correlationId", correlationId);
    if (shopId != null && !shopId.isBlank()) {
      MDC.put("shopId", shopId);
    }

    response.setHeader(HDR_CORRELATION, correlationId);

    try {
      filterChain.doFilter(request, response);
    } finally {
      MDC.clear();
    }
  }
}
