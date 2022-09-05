package com.kristijan.iotdesk.messaging.mqtt;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@SuppressWarnings("rawtypes")
public abstract class MqttContainerTest {

  private static final int MOSQUITTO_PORT = 1883;

  protected static final GenericContainer mqttContainer;

  static {
    mqttContainer = new GenericContainer(DockerImageName.parse("eclipse-mosquitto:2.0.15"))
      .withCopyFileToContainer(MountableFile.forClasspathResource("mosquitto.conf"), "/mosquitto/config/mosquitto.conf")
      .withExposedPorts(MOSQUITTO_PORT);
    mqttContainer.start();
  }

  @DynamicPropertySource
  static void registerMqttProperties(DynamicPropertyRegistry registry) {
    registry.add("mqtt.broker-url", () ->
      String.format("tcp://%s:%d", mqttContainer.getHost(), mqttContainer.getMappedPort(MOSQUITTO_PORT)));
    registry.add("mqtt.client-id", () -> "testClient");
  }
}
