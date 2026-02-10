package com.marioalba.booking.common.web;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

  @Bean
  public FilterRegistrationBean<CorrelationIdFilter> correlationIdFilter() {
    FilterRegistrationBean<CorrelationIdFilter> bean = new FilterRegistrationBean<>();
    bean.setFilter(new CorrelationIdFilter());
    bean.setOrder(1);
    return bean;
  }
}
