package com.kristijan.iotdesk.messaging.mqtt;

import com.kristijan.iotdesk.domain.snapshots.services.AddDeviceSnapshotService;
import com.kristijan.iotdesk.domain.snapshots.services.DeviceMessagingErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.mockito.Mockito.mock;

@Configuration
@ComponentScan(basePackageClasses = MqttClientComponents.class)
public class MqttIntegrationTestConfig {

  public static final LocalDateTime NOW = LocalDateTime.parse("2022-09-02T14:07:00");

  @Bean
  public Clock clock() {
    return Clock.fixed(NOW.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);
  }

  /**
   * Providing mock transaction manager since there is no data source in the context of the mqtt integration tests.
   */
  @Bean
  public PlatformTransactionManager platformTransactionManager() {
    return mock(PlatformTransactionManager.class);
  }

  @Bean
  public AddDeviceSnapshotService addDeviceSnapshotService() {
    return mock(AddDeviceSnapshotService.class);
  }

  @Bean
  public DeviceMessagingErrorHandler deviceMessagingErrorHandler() {
    return mock(DeviceMessagingErrorHandler.class);
  }
}
