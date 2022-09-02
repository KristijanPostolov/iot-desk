package com.kristijan.iotdesk.messaging.mqtt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZonedDateTime;

@Configuration
@ComponentScan(basePackageClasses = MqttClientComponents.class)
public class MqttIntegrationTestConfig {

  public static final ZonedDateTime NOW = ZonedDateTime.parse("2022-09-02T14:07:00Z");

  @Bean
  public Clock clock() {
    return Clock.fixed(NOW.toInstant(), NOW.getZone());
  }

}
