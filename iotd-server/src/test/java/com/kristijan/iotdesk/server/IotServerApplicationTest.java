package com.kristijan.iotdesk.server;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Clock;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class IotServerApplicationTest {

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