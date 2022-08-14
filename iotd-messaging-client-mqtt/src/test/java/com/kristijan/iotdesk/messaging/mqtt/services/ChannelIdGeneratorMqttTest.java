package com.kristijan.iotdesk.messaging.mqtt.services;


import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@ExtendWith(MockitoExtension.class)
public class ChannelIdGeneratorMqttTest {

  private final ChannelIdGeneratorMqtt channelIdGeneratorMqtt = new ChannelIdGeneratorMqtt();

  @Test
  void shouldReturnChannelIfWithUUIDFormat() {
    String channelId = channelIdGeneratorMqtt.generate();

    assertFalse(StringUtils.isBlank(channelId));
    assertEquals(36, channelId.length());
  }

  @Test
  void shouldReturnUniqueResults() {
    Set<String> results = new HashSet<>();
    for (int i = 0; i < 100; i++) {
      results.add(channelIdGeneratorMqtt.generate());
    }

    assertEquals(100, results.size());
  }
}
