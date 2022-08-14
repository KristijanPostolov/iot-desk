package com.kristijan.iotdesk.domain.snapshots.services;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DeviceMessagingErrorHandler {

  public void nonExistingChannelId(String channelId) {
    log.error("Received message for non existing channel id: {}", channelId);
  }

  public void invalidPayload(String channelId) {
    log.error("Received invalid payload for channel id: {}", channelId);
  }

  public void nonExistingDevice(long deviceId) {
    log.error("Fatal error: Received message for valid channelId, but missing device: " + deviceId);
  }
}
