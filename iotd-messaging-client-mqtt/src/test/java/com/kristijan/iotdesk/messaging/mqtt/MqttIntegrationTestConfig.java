package com.kristijan.iotdesk.messaging.mqtt;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Clock;
import java.time.ZonedDateTime;

import static org.mockito.Mockito.mock;

@Configuration
@ComponentScan(basePackageClasses = MqttClientComponents.class)
public class MqttIntegrationTestConfig {

  public static final ZonedDateTime NOW = ZonedDateTime.parse("2022-09-02T14:07:00Z");

  @Bean
  public Clock clock() {
    return Clock.fixed(NOW.toInstant(), NOW.getZone());
  }

  /**
   * Providing mock transaction manager since there is no data source in the context of the mqtt integration tests.
   */
  @Bean
  public PlatformTransactionManager platformTransactionManager() {
    return mock(PlatformTransactionManager.class);
  }

}
