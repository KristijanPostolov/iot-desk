package com.kristijan.iotdesk.messaging.mqtt.services;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@Testcontainers
public abstract class MqttContainerTest {

  private static final int MOSQUITTO_PORT = 1883;

  @Container
  protected static GenericContainer mqttContainer = new GenericContainer(DockerImageName.parse("eclipse-mosquitto:2.0.15"))
    .withCopyFileToContainer(MountableFile.forClasspathResource("mosquitto.conf"), "/mosquitto/config/mosquitto.conf")
    .withExposedPorts(MOSQUITTO_PORT);

  @DynamicPropertySource
  static void registerMqttProperties(DynamicPropertyRegistry registry) {
    registry.add("mqtt.broker-url", () ->
      String.format("tcp://%s:%d", mqttContainer.getHost(), mqttContainer.getMappedPort(MOSQUITTO_PORT)));
    registry.add("mqtt.client-id", () -> "testClient");
  }
}
