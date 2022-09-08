package com.kristijan.iotdesk.domain.snapshots.services;

import com.kristijan.iotdesk.domain.device.exceptions.DomainException;
import com.kristijan.iotdesk.domain.device.exceptions.TransientDomainException;
import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceChannelId;
import com.kristijan.iotdesk.domain.device.models.DeviceState;
import com.kristijan.iotdesk.domain.device.services.ChannelIdService;
import com.kristijan.iotdesk.domain.device.services.ListDevicesService;
import com.kristijan.iotdesk.domain.snapshots.models.AcknowledgementStatus;
import com.kristijan.iotdesk.domain.snapshots.models.CommandData;
import com.kristijan.iotdesk.domain.snapshots.models.CommandRequest;
import com.kristijan.iotdesk.domain.snapshots.models.DeviceCommand;
import com.kristijan.iotdesk.domain.snapshots.ports.DeviceCommandSender;
import com.kristijan.iotdesk.domain.snapshots.repositories.DeviceCommandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class DeviceCommandService {

  private final ListDevicesService listDevicesService;
  private final ChannelIdService channelIdService;
  private final DeviceCommandSender deviceCommandSender;
  private final DeviceCommandRepository deviceCommandRepository;
  private final Clock clock;

  public void postDeviceCommand(CommandRequest commandRequest) {
    long deviceId = commandRequest.getDeviceId();
    checkDeviceState(deviceId);
    String channelId = getChannelId(deviceId);
    String commandId = generateCommandId();

    DeviceCommand deviceCommand = new DeviceCommand(commandId, commandRequest.getContent(), deviceId,
      LocalDateTime.now(clock), AcknowledgementStatus.NO_ACK);
    sendToDevice(channelId, deviceCommand);
    deviceCommandRepository.save(deviceCommand);
  }

  public List<DeviceCommand> getCommandsInTimeRange(long deviceId, LocalDateTime beginRange, LocalDateTime endRange) {
    return deviceCommandRepository.findByDeviceIdAndSentAtTimeRangeOrderedAscending(deviceId, beginRange, endRange);
  }

  private void sendToDevice(String channelId, DeviceCommand deviceCommand) {
    try {
      CommandData commandData = new CommandData(deviceCommand.getCommandId(), deviceCommand.getContent());
      log.info("Sending command with id: " + deviceCommand.getCommandId() + ", to device: " +
        deviceCommand.getDeviceId());
      deviceCommandSender.sendCommandToDevice(channelId, commandData);
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
