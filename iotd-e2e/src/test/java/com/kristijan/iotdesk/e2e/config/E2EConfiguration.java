package com.kristijan.iotdesk.e2e.config;

import com.kristijan.iotdesk.e2e.E2EComponents;
import lombok.SneakyThrows;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableConfigurationProperties
@ComponentScan(basePackageClasses = E2EComponents.class)
public class E2EConfiguration {

  @Bean
  public RestTemplate restTemplate() {
    return new RestTemplate();
  }

  @Bean
  @SneakyThrows
  public MqttClient mqttClient(E2ETestProperties properties) {
    MqttClient client = new MqttClient(properties.getMqttBrokerUrl(), properties.getMqttClientId());
    client.connect();
    return client;
  }
}
