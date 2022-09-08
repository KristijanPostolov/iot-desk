package com.kristijan.iotdesk.application.services;

import com.kristijan.iotdesk.application.dtos.CreateCommandDto;
import com.kristijan.iotdesk.application.dtos.DeviceCommandDto;
import com.kristijan.iotdesk.domain.snapshots.models.AcknowledgementStatus;
import com.kristijan.iotdesk.domain.snapshots.models.CommandRequest;
import com.kristijan.iotdesk.domain.snapshots.models.DeviceCommand;
import com.kristijan.iotdesk.domain.snapshots.services.DeviceCommandService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandsApplicationServiceTest {

  @InjectMocks
  private CommandsApplicationService commandsApplicationService;

  @Mock
  private DeviceCommandService deviceCommandService;

  @Mock
  private Clock clock;

  @Test
  void shouldPostCommand() {
    commandsApplicationService.postCommand(new CreateCommandDto(1L, "content"));

    ArgumentCaptor<CommandRequest> commandRequestCaptor = ArgumentCaptor.forClass(CommandRequest.class);
    verify(deviceCommandService).postDeviceCommand(commandRequestCaptor.capture());
    assertEquals(1L, commandRequestCaptor.getValue().getDeviceId());
    assertEquals("content", commandRequestCaptor.getValue().getContent());
  }

  @Test
  void shouldGetCommandsByDeviceIdAndTimestamps() {
    when(clock.getZone()).thenReturn(ZoneOffset.UTC);
    LocalDateTime localNow = LocalDateTime.parse("2022-09-08T09:48:00");
    List<DeviceCommand> savedCommands = List.of(
      new DeviceCommand("id1", "content1", 1L, localNow.plusMinutes(5), AcknowledgementStatus.NO_ACK),
      new DeviceCommand(2L, "id1", "content1", 1L, localNow.plusMinutes(5), localNow.plusMinutes(6),
        AcknowledgementStatus.ACK_SUCCESSFUL));
    when(deviceCommandService.getCommandsInTimeRange(1L, localNow, localNow.plusMinutes(30)))
      .thenReturn(savedCommands);

    ZonedDateTime now = ZonedDateTime.parse("2022-09-08T11:48:00+02:00");
    List<DeviceCommandDto> results = commandsApplicationService.getDeviceCommands(1L, now, now.plusMinutes(30));

    assertEquals(savedCommands.size(), results.size());
    assertDeviceCommandDto(savedCommands.get(0), results.get(0));
  }

  private void assertDeviceCommandDto(DeviceCommand expected, DeviceCommandDto dto) {
    assertNotNull(dto);
    assertEquals(expected.getCommandId(), dto.getCommandId());
    assertEquals(expected.getContent(), dto.getContent());
    assertEquals(toZoned(expected.getSentAt(), clock.getZone()), dto.getSentAt());
    assertEquals(toZoned(expected.getAcknowledgedAt(), clock.getZone()), dto.getAcknowledgedAt());
    assertEquals(expected.getAckStatus(), dto.getAckStatus());
  }

  private ZonedDateTime toZoned(LocalDateTime local, ZoneId zoneId) {
    return local == null ? null : local.atZone(zoneId);
  }

}