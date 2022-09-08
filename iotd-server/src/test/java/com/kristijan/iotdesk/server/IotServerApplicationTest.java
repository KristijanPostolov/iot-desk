package com.kristijan.iotdesk.server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

import java.time.Clock;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SuppressWarnings("rawtypes")
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class IotServerApplicationTest {

  private static final int MOSQUITTO_PORT = 1883;
  private static final int POSTGRES_PORT = 5432;
  private static final String POSTGRES_USER = "iotdesk";
  private static final String POSTGRES_PASSWORD = "iotdesk";

  private static final GenericContainer mqttContainer;
  private static final GenericContainer postgresContainer;

  static {
    mqttContainer = new GenericContainer(DockerImageName.parse("eclipse-mosquitto:2.0.15"))
      .withCopyFileToContainer(MountableFile.forClasspathResource("mosquitto.conf"), "/mosquitto/config/mosquitto.conf")
      .withExposedPorts(MOSQUITTO_PORT);
    mqttContainer.start();

    postgresContainer = new GenericContainer(DockerImageName.parse("postgres:14.5"))
      .withEnv("POSTGRES_USER", POSTGRES_USER)
      .withEnv("POSTGRES_PASSWORD", POSTGRES_PASSWORD)
      .withExposedPorts(POSTGRES_PORT)
      .withReuse(true);
    postgresContainer.start();
  }

  @DynamicPropertySource
  static void registerMqttProperties(DynamicPropertyRegistry registry) {
    registry.add("mqtt.broker-url", () ->
      String.format("tcp://%s:%d", mqttContainer.getHost(), mqttContainer.getMappedPort(MOSQUITTO_PORT)));

    registry.add("spring.datasource.url", () -> String.format("jdbc:postgresql://%s:%d/postgres",
      postgresContainer.getHost(), postgresContainer.getMappedPort(POSTGRES_PORT)));
    registry.add("spring.datasource.username", () -> POSTGRES_USER);
    registry.add("spring.datasource.password", () -> POSTGRES_PASSWORD);
  }

  @Autowired
  private Clock clock;

  @Test
  void contextLoads() {
  }

  @Test
  void clockShouldBeInUTC() {
    assertEquals(ZoneOffset.UTC, clock.getZone());
  }
}