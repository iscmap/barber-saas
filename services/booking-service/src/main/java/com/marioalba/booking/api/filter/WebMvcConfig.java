package com.marioalba.booking.api.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class WebMvcConfig implements WebMvcConfigurer {
  private final RequiredHeadersInterceptor requiredHeadersInterceptor;

  public WebMvcConfig(RequiredHeadersInterceptor requiredHeadersInterceptor) {
    this.requiredHeadersInterceptor = requiredHeadersInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry
        .addInterceptor(requiredHeadersInterceptor)
        .addPathPatterns("/**")
        .excludePathPatterns("/actuator/**");
  }
}
