package com.kristijan.iotdesk.messaging.mqtt.config;

import com.kristijan.iotdesk.messaging.mqtt.exceptions.MqttRuntimeException;
import com.kristijan.iotdesk.messaging.mqtt.services.MqttMessageHandler;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Set;

@Configuration
@EnableTransactionManagement
public class MqttConfiguration {

  @Value("${mqtt.broker-url}")
  private String brokerUrl;
  @Value("${mqtt.client-id}")
  private String clientId;
  @Value("${mqtt.clean-session:false}")
  private boolean cleanSession;
  @Value("${mqtt.use-persistence:false}")
  private boolean usePersistence;

  @Bean
  public MqttClient mqttClient(Set<MqttMessageHandler> handlers) {
    try {
      MqttConnectOptions connectOptions = new MqttConnectOptions();
      connectOptions.setCleanSession(cleanSession);
      MqttClientPersistence clientPersistence = usePersistence ? new MqttDefaultFilePersistence() : null;
      MqttClient mqttClient = new MqttClient(brokerUrl, clientId, clientPersistence);
      mqttClient.connect(connectOptions);

      handlers.forEach(handler -> subscribeHandler(mqttClient, handler));
      return mqttClient;
    } catch (MqttException e) {
      throw new MqttRuntimeException("Error while creating mqtt client", e);
    }
  }

  private void subscribeHandler(MqttClient mqttClient, MqttMessageHandler handler) {
    try {
      mqttClient.subscribe(handler.getTopicFilter(), handler::handleMqttMessage);
    } catch (MqttException e) {
      throw new MqttRuntimeException("Failed to subscribe mqtt message handler: " + handler.getClass(), e);
    }
  }
}
