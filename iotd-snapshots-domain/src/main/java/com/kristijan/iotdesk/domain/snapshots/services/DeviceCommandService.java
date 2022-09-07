package com.kristijan.iotdesk.domain.snapshots.services;

import com.kristijan.iotdesk.domain.device.exceptions.DomainException;
import com.kristijan.iotdesk.domain.device.exceptions.TransientDomainException;
import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceChannelId;
import com.kristijan.iotdesk.domain.device.models.DeviceState;
import com.kristijan.iotdesk.domain.device.services.ChannelIdService;
import com.kristijan.iotdesk.domain.device.services.ListDevicesService;
import com.kristijan.iotdesk.domain.snapshots.models.CommandData;
import com.kristijan.iotdesk.domain.snapshots.models.CommandRequest;
import com.kristijan.iotdesk.domain.snapshots.ports.DeviceCommandSender;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class DeviceCommandService {

  private final ListDevicesService listDevicesService;
  private final ChannelIdService channelIdService;
  private final DeviceCommandSender deviceCommandSender;

  public void postDeviceCommand(CommandRequest commandRequest) {
    long deviceId = commandRequest.getDeviceId();
    checkDeviceState(deviceId);
    String channelId = getChannelId(deviceId);
    String commandId = generateCommandId();
    try {
      deviceCommandSender.sendCommandToDevice(channelId, new CommandData(commandId, commandRequest.getContent()));
    } catch (Exception e) {
      throw new TransientDomainException("Failed sending device message", e);
    }
  }

  private String getChannelId(long deviceId) {
    return channelIdService.findByDeviceId(deviceId)
      .map(DeviceChannelId::getChannelId)
      .orElseThrow(() -> new DomainException("Fatal error: Could not find channel id for device with id: " + deviceId));
  }

  private void checkDeviceState(long deviceId) {
    Device device = listDevicesService.findById(deviceId)
      .orElseThrow(() -> new DomainException("Posting command for non existing device id: " + deviceId));
    if (!DeviceState.ACTIVE.equals(device.getState())) {
      throw new DomainException("Posting command for device " + deviceId + " with state: " + device.getState());
    }
  }

  private String generateCommandId() {
    return UUID.randomUUID().toString();
  }
}
