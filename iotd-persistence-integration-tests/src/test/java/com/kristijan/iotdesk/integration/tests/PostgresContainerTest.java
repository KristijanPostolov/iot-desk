package com.kristijan.iotdesk.integration.tests;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

@SuppressWarnings("rawtypes")
public class PostgresContainerTest {

  private static final int POSTGRES_PORT = 5432;
  private static final String POSTGRES_USER = "iotdesk";
  private static final String POSTGRES_PASSWORD = "iotdesk";

  protected static final GenericContainer postgresContainer;

  static {
    postgresContainer = new GenericContainer(DockerImageName.parse("postgres:14.5"))
      .withEnv("POSTGRES_USER", POSTGRES_USER)
      .withEnv("POSTGRES_PASSWORD", POSTGRES_PASSWORD)
      .withExposedPorts(POSTGRES_PORT)
      .withReuse(true);
    postgresContainer.start();
  }

  @DynamicPropertySource
  static void registerMqttProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", () -> String.format("jdbc:postgresql://%s:%d/postgres",
      postgresContainer.getHost(), postgresContainer.getMappedPort(POSTGRES_PORT)));
    registry.add("spring.datasource.username", () -> POSTGRES_USER);
    registry.add("spring.datasource.password", () -> POSTGRES_PASSWORD);
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");
  }

}
