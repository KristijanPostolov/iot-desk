package com.kristijan.iotdesk.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Clock;

@Configuration
@EnableTransactionManagement
public class ApplicationConfiguration {
  @Bean
  public Clock clock() {
    return Clock.systemUTC();
  }
}
