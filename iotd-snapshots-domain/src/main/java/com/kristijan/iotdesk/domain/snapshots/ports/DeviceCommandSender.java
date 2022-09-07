package com.kristijan.iotdesk.domain.snapshots.ports;

import com.kristijan.iotdesk.domain.snapshots.models.CommandData;

/**
 * Interface for a service that sends commands to devices.
 */
public interface DeviceCommandSender {

  /**
   * Sends the deviceCommand to the device with the given channelId.
   *
   * @param channelId the channel id of the device.
   * @param commandData the command to be sent.
   */
  void sendCommandToDevice(String channelId, CommandData commandData);

}
