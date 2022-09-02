package com.kristijan.iotdesk.simulator;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "simulator")
public class SimulatorConfiguration {
  private String brokerUrl;
  private String clientIdPrefix;
}
