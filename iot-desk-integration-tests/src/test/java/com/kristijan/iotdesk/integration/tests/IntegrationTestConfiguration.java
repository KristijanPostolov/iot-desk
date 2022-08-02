package com.kristijan.iotdesk.integration.tests;

import com.kristijan.iotdesk.domain.device.ports.ChannelIdGenerator;
import com.kristijan.iotdesk.persistence.mock.PersistenceMockComponents;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;

@SpringBootConfiguration
@Import({DeviceDomainConfiguration.class, UserDomainConfiguration.class})
@ComponentScan(basePackageClasses = PersistenceMockComponents.class)
public class IntegrationTestConfiguration {

  @Bean
  public Clock clock() {
    return Clock.fixed(Instant.parse("2022-07-24T16:00:00Z"), ZoneOffset.UTC);
  }

  @Bean
  public ChannelIdGenerator channelIdGenerator() {
    return () -> UUID.randomUUID().toString();
  }

}
