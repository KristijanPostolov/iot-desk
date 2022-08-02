package com.kristijan.iotdesk.domain.device.ports;

/**
 * Interface for generation of channel ids. Should be implemented by the module that handles the communication channel
 * between the application and devices.
 */
public interface ChannelIdGenerator {
  String generate();
}
