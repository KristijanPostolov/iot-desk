package com.kristijan.iotdesk.messaging.mqtt.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
public class MqttConfiguration {

  @Value("${mqtt.broker-url}")
  private String brokerUrl;
  @Value("${mqtt.client-id}")
  private String clientId;

  @Bean
  public MqttClient mqttClient() {
    try {
      MqttConnectOptions connectOptions = new MqttConnectOptions();
      connectOptions.setCleanSession(false);
      MqttClient mqttClient = new MqttClient(brokerUrl, clientId);
      mqttClient.connect(connectOptions);
      return mqttClient;
    } catch (MqttException e) {
      throw new RuntimeException("Error while creating mqtt client", e);
    }
  }
}
