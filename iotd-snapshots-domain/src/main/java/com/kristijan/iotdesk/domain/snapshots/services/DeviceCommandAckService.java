package com.kristijan.iotdesk.domain.snapshots.services;

import com.kristijan.iotdesk.domain.device.models.DeviceChannelId;
import com.kristijan.iotdesk.domain.device.services.ChannelIdService;
import com.kristijan.iotdesk.domain.snapshots.models.AcknowledgementStatus;
import com.kristijan.iotdesk.domain.snapshots.models.CommandAck;
import com.kristijan.iotdesk.domain.snapshots.models.DeviceCommand;
import com.kristijan.iotdesk.domain.snapshots.repositories.DeviceCommandRepository;
import lombok.RequiredArgsConstructor;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service that manages device command acknowledgements.
 */
@RequiredArgsConstructor
public class DeviceCommandAckService {

  private final ChannelIdService channelIdService;
  private final DeviceMessagingErrorHandler deviceMessagingErrorHandler;
  private final DeviceCommandRepository deviceCommandRepository;
  private final Clock clock;
  /**
   * Marks a device command as acknowledged.
   *
   * @param channelId the channelId for which the acknowledgement was received.
   * @param commandAck the acknowledgement message.
   * @return true if command was successfully acknowledged.
   */
  public boolean acknowledgeCommand(String channelId, CommandAck commandAck) {
    Optional<DeviceChannelId> deviceChannelIdOptional = channelIdService.findByChannelId(channelId);
    if (deviceChannelIdOptional.isEmpty()) {
      deviceMessagingErrorHandler.nonExistingChannelId(channelId);
      return false;
    }
    long deviceId = deviceChannelIdOptional.get().getDeviceId();

    String commandId = commandAck.getCommandId();
    Optional<DeviceCommand> deviceCommandOptional = deviceCommandRepository.findByCommandId(commandId);
    if (deviceCommandOptional.isEmpty()) {
      deviceMessagingErrorHandler.ackForNonExistingDeviceCommand(commandId, channelId);
      return false;
    }
    DeviceCommand deviceCommand = deviceCommandOptional.get();

    if (deviceCommand.getDeviceId() != deviceId) {
      deviceMessagingErrorHandler.ackReceivedForExistingCommandOnDifferentChannel(commandId, channelId);
      return false;
    }

    AcknowledgementStatus status = commandAck.isSuccessful() ? AcknowledgementStatus.ACK_SUCCESSFUL
      : AcknowledgementStatus.ACK_FAILED;
    deviceCommand.setAckStatus(status);
    deviceCommand.setAcknowledgedAt(LocalDateTime.now(clock));
    deviceCommandRepository.save(deviceCommand);
    return true;
  }
}
