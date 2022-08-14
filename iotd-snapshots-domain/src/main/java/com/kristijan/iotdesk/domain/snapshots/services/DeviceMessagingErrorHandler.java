package com.kristijan.iotdesk.domain.snapshots.services;

import lombok.extern.slf4j.Slf4j;

/**
 * Service that handles exceptional cases during processing of messages from devices.
 */
@Slf4j
public class DeviceMessagingErrorHandler {

  public void nonExistingChannelId(String channelId) {
    log.error("Received message for non existing channel id: {}", channelId);
  }

  public void nonExistingDevice(long deviceId) {
    log.error("Fatal error: Received message for valid channel id, but missing device: " + deviceId);
  }

  public void invalidPayload(String channelId) {
    log.error("Received invalid payload for channel id: {}", channelId);
  }

  public void handleDomainException(String channelId, Exception exception) {
    log.error("Fatal error: Exception occurred while processing message for channel id: " + channelId, exception);
  }
}
