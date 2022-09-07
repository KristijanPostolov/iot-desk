package com.kristijan.iotdesk.integration.tests;

import com.kristijan.iotdesk.domain.device.ports.ChannelIdGenerator;
import com.kristijan.iotdesk.domain.snapshots.ports.DeviceCommandSender;
import com.kristijan.iotdesk.jpa.PersistenceJpaComponents;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.mockito.Mockito.mock;

@SpringBootConfiguration
@Import({DeviceDomainConfiguration.class, UserDomainConfiguration.class, SnapshotsDomainConfiguration.class})
@ComponentScan(basePackageClasses = PersistenceJpaComponents.class)
@EnableAutoConfiguration
public class IntegrationTestConfiguration {

  @Bean
  public Clock clock() {
    return Clock.fixed(Instant.parse("2022-07-24T16:00:00Z"), ZoneOffset.UTC);
  }

  @Bean
  public ChannelIdGenerator channelIdGenerator() {
    return () -> UUID.randomUUID().toString();
  }

  @Bean
  public DeviceCommandSender deviceCommandSender() {
    return mock(DeviceCommandSender.class);
  }

}
