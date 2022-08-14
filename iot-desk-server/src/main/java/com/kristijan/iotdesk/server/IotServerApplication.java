package com.kristijan.iotdesk.server;

import com.kristijan.iotdesk.application.ApplicationComponents;
import com.kristijan.iotdesk.messaging.mqtt.MqttClientComponents;
import com.kristijan.iotdesk.persistence.mock.PersistenceMockComponents;
import com.kristijan.iotdesk.server.config.DomainConfigComponents;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {DomainConfigComponents.class, ApplicationComponents.class,
  PersistenceMockComponents.class, MqttClientComponents.class})
public class IotServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(IotServerApplication.class, args);
  }
}
