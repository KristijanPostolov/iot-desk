package com.kristijan.iotdesk.e2e.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "e2e")
public class E2ETestProperties {

  private String serverUrl;
  private String mqttBrokerUrl;
  private String mqttClientId;
}
