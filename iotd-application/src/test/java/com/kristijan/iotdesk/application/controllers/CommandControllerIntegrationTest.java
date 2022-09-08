package com.kristijan.iotdesk.application.controllers;

import com.kristijan.iotdesk.application.dtos.CreateCommandDto;
import com.kristijan.iotdesk.application.dtos.DeviceCommandDto;
import com.kristijan.iotdesk.application.services.CommandsApplicationService;
import com.kristijan.iotdesk.domain.device.exceptions.DomainException;
import com.kristijan.iotdesk.domain.snapshots.models.AcknowledgementStatus;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommandController.class)
public class CommandControllerIntegrationTest {

  public static final String COMMANDS_API = "/api/v1/commands";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private CommandsApplicationService commandsApplicationService;

  @Test
  @SneakyThrows
  void shouldPostDeviceCommand() {
    MockHttpServletRequestBuilder request = post(COMMANDS_API)
      .content("{\"deviceId\": 1, \"commandContent\": \"1:2\"}")
      .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(request)
      .andExpect(status().isOk());
    ArgumentCaptor<CreateCommandDto> captor = ArgumentCaptor.forClass(CreateCommandDto.class);
    verify(commandsApplicationService).postCommand(captor.capture());
    assertEquals(1L, captor.getValue().getDeviceId());
    assertEquals("1:2", captor.getValue().getCommandContent());
  }

  @Test
  @SneakyThrows
  void shouldReturnBadRequestOnDomainException() {
    doThrow(new DomainException("")).when(commandsApplicationService).postCommand(any());
    MockHttpServletRequestBuilder request = post(COMMANDS_API)
      .content("{\"deviceId\": 1, \"commandContent\": \"1:2\"}")
      .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(request)
      .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldGetDeviceCommands() {
    long deviceId = 1L;
    ZonedDateTime beginRange = ZonedDateTime.parse("2022-09-08T13:00:00Z");
    ZonedDateTime endRange = ZonedDateTime.parse("2022-09-08T14:00:00Z");
    when(commandsApplicationService.getDeviceCommands(deviceId, beginRange, endRange))
      .thenReturn(List.of(
        new DeviceCommandDto("id1", "command1", beginRange.plusMinutes(1), null, AcknowledgementStatus.NO_ACK),
        new DeviceCommandDto("id2", "command2", beginRange.plusMinutes(2), beginRange.plusMinutes(3), AcknowledgementStatus.ACK_SUCCESSFUL)
        ));


    MockHttpServletRequestBuilder request = get(COMMANDS_API)
      .param("deviceId", String.valueOf(deviceId))
      .param("beginRange", beginRange.format(DateTimeFormatter.ISO_DATE_TIME))
      .param("endRange", endRange.format(DateTimeFormatter.ISO_DATE_TIME));


    String expectedJson = "[" +
      "{\"commandId\": \"id1\", \"content\": \"command1\", \"sentAt\": \"2022-09-08T13:01:00Z\", " +
        "\"acknowledgedAt\": null, \"ackStatus\": \"NO_ACK\"}," +
      "{\"commandId\": \"id2\", \"content\": \"command2\", \"sentAt\": \"2022-09-08T13:02:00Z\", " +
        "\"acknowledgedAt\":\"2022-09-08T13:03:00Z\", \"ackStatus\": \"ACK_SUCCESSFUL\"}" +
      "]";
    mockMvc.perform(request)
      .andExpect(status().isOk())
      .andExpect(content().json(expectedJson));
  }

  @Test
  @SneakyThrows
  void shouldReturnBadRequestWhenFetchingWithoutDeviceId() {
    ZonedDateTime beginRange = ZonedDateTime.parse("2022-09-08T13:00:00Z");
    ZonedDateTime endRange = ZonedDateTime.parse("2022-09-08T14:00:00Z");
    MockHttpServletRequestBuilder request = get(COMMANDS_API)
      .param("beginRange", beginRange.format(DateTimeFormatter.ISO_DATE_TIME))
      .param("endRange", endRange.format(DateTimeFormatter.ISO_DATE_TIME));

    mockMvc.perform(request)
      .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturnBadRequestWhenFetchingWithoutTimestamps() {
    MockHttpServletRequestBuilder request = get(COMMANDS_API)
      .param("deviceId", "1");

    mockMvc.perform(request)
      .andExpect(status().isBadRequest());
  }
}
