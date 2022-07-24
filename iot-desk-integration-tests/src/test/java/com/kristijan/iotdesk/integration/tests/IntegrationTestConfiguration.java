package com.kristijan.iotdesk.integration.tests;

import com.kristijan.iotdesk.persistence.mock.PersistenceMockComponents;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

@SpringBootConfiguration
@Import(DeviceDomainConfiguration.class)
@ComponentScan(basePackageClasses = PersistenceMockComponents.class)
public class IntegrationTestConfiguration {

  @Bean
  public Clock clock() {
    return Clock.fixed(Instant.parse("2022-07-24T16:00:00Z"), ZoneOffset.UTC);
  }

}
