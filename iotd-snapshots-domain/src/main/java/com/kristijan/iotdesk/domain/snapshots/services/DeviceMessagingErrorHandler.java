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

  public void invalidSnapshotPayload(String channelId) {
    log.error("Received invalid snapshot payload for channel id: {}", channelId);
  }

  public void handleSnapshotDomainException(String channelId, Exception ex) {
    log.error("Fatal error: Exception occurred while processing snapshot message for channel id: " + channelId, ex);
  }

  public void invalidCommandAckPayload(String channelId) {
    log.error("Received invalid command acknowledgement payload for channel id: {}", channelId);
  }

  public void handleCommandAckDomainException(String channelId, Exception ex) {
    log.error("Fatal error: Exception occurred while processing command acknowledgement message for channel id: " +
      channelId, ex);
  }

  public void ackForNonExistingDeviceCommand(String commandId, String channelId) {
    log.error("Received acknowledgement for non existing command id: " + commandId + ", on channel id: " + channelId);
  }

  public void ackReceivedForExistingCommandOnDifferentChannel(String commandId, String channelId) {
    log.error("Fatal error: Received acknowledgement for existing command id and channel id, but they are " +
      "referencing different devices. Command id: " + commandId + ", channel id: " + channelId);

  }
}
