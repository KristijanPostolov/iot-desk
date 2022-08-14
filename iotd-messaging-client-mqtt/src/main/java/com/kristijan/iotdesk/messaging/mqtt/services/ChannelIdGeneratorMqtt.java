package com.kristijan.iotdesk.messaging.mqtt.services;


import com.kristijan.iotdesk.domain.device.ports.ChannelIdGenerator;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Implementation of {@link ChannelIdGenerator}, which returns a UUID as channel id. This will be part of the mqtt
 * topic for communication with the device id.
 */
@Service
public class ChannelIdGeneratorMqtt implements ChannelIdGenerator {
  @Override
  public String generate() {
    return UUID.randomUUID().toString();
  }
}
