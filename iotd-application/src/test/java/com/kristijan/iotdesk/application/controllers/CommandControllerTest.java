package com.kristijan.iotdesk.application.controllers;

import com.kristijan.iotdesk.application.dtos.CreateCommandDto;
import com.kristijan.iotdesk.application.dtos.DeviceCommandDto;
import com.kristijan.iotdesk.application.services.CommandsApplicationService;
import com.kristijan.iotdesk.domain.snapshots.models.AcknowledgementStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandControllerTest {

  @InjectMocks
  private CommandController commandController;

  @Mock
  private CommandsApplicationService commandsApplicationService;

  @Test
  void shouldPostCommand() {
    CreateCommandDto request = new CreateCommandDto(1L, "1:2");
    commandController.postCommand(request);

    verify(commandsApplicationService).postCommand(request);
  }

  @Test
  void shouldGetDeviceCommands() {
    ZonedDateTime now = ZonedDateTime.now();
    List<DeviceCommandDto> listToReturn = List.of(new DeviceCommandDto("id", "content",
      now.plusMinutes(1), now.plusMinutes(5), AcknowledgementStatus.NO_ACK));
    when(commandsApplicationService.getDeviceCommands(1L, now, now.plusMinutes(10)))
      .thenReturn(listToReturn);

    List<DeviceCommandDto> result = commandController.getDeviceCommands(1L, now, now.plusMinutes(10));

    assertEquals(listToReturn, result);
  }
}