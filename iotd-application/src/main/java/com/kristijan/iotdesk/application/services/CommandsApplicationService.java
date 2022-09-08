package com.kristijan.iotdesk.application.services;

import com.kristijan.iotdesk.application.dtos.CreateCommandDto;
import com.kristijan.iotdesk.application.dtos.DeviceCommandDto;
import com.kristijan.iotdesk.domain.snapshots.models.CommandRequest;
import com.kristijan.iotdesk.domain.snapshots.models.DeviceCommand;
import com.kristijan.iotdesk.domain.snapshots.services.DeviceCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommandsApplicationService {

  private final DeviceCommandService deviceCommandService;
  private final Clock clock;

  public void postCommand(CreateCommandDto createCommandDto) {
    CommandRequest commandRequest = new CommandRequest(createCommandDto.getDeviceId(),
      createCommandDto.getCommandContent());
    deviceCommandService.postDeviceCommand(commandRequest);
  }

  public List<DeviceCommandDto> getDeviceCommands(Long deviceId, ZonedDateTime beginRange, ZonedDateTime endRange) {
    return deviceCommandService.getCommandsInTimeRange(deviceId, toLocal(beginRange), toLocal(endRange)).stream()
      .map(this::mapCommandToDto)
      .collect(Collectors.toList());
  }

  private LocalDateTime toLocal(ZonedDateTime timestamp) {
    return LocalDateTime.ofInstant(timestamp.toInstant(), clock.getZone());
  }

  private DeviceCommandDto mapCommandToDto(DeviceCommand command) {
    return new DeviceCommandDto(
      command.getCommandId(),
      command.getContent(),
      toZoned(command.getSentAt()),
      toZoned(command.getAcknowledgedAt()),
      command.getAckStatus());
  }

  private ZonedDateTime toZoned(LocalDateTime timestamp) {
    return timestamp == null ? null : timestamp.atZone(clock.getZone());
  }

}
