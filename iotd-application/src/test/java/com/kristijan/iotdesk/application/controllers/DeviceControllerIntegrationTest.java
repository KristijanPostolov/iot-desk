package com.kristijan.iotdesk.application.controllers;

import com.kristijan.iotdesk.application.dtos.ChannelIdDto;
import com.kristijan.iotdesk.application.dtos.CreateDeviceResponseDto;
import com.kristijan.iotdesk.application.dtos.DeviceDetailsDto;
import com.kristijan.iotdesk.application.dtos.DeviceDto;
import com.kristijan.iotdesk.application.dtos.DeviceParameterDto;
import com.kristijan.iotdesk.application.exceptions.NotFoundException;
import com.kristijan.iotdesk.application.services.DevicesApplicationService;
import com.kristijan.iotdesk.domain.device.models.DeviceState;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.ZonedDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DeviceController.class)
public class DeviceControllerIntegrationTest {

  public static final String DEVICES_API = "/api/v1/devices";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private DevicesApplicationService devicesApplicationService;

  @Test
  @SneakyThrows
  void shouldReturnEmptyList() {
    MockHttpServletRequestBuilder request = get(DEVICES_API);

    mockMvc.perform(request)
      .andExpect(status().isOk())
      .andExpect(content().json("[]", true));
  }

  @Test
  @SneakyThrows
  void shouldReturnListOfDevices() {
    when(devicesApplicationService.getAllDevices()).thenReturn(List.of(
        new DeviceDto(1L, "d1", DeviceState.NEW),
        new DeviceDto(2L, "d2", DeviceState.NEW)));
    MockHttpServletRequestBuilder request = get(DEVICES_API);


    String expectedJson = "[" +
      "{\"id\": 1, \"name\": \"d1\", \"state\": \"NEW\"}," +
      "{\"id\": 2, \"name\": \"d2\", \"state\": \"NEW\"}" +
      "]";
    mockMvc.perform(request)
      .andExpect(status().isOk())
      .andExpect(content().json(expectedJson));
  }

  @Test
  @SneakyThrows
  void shouldCreateNewDevice() {
    when(devicesApplicationService.createNewDevice(any())).thenReturn(new CreateDeviceResponseDto(1L));

    MockHttpServletRequestBuilder request = post(DEVICES_API)
      .content("{\"name\": \"New Device\"}")
      .contentType(MediaType.APPLICATION_JSON);

    String expectedJson = "{\"id\": 1}";
    mockMvc.perform(request)
      .andExpect(status().isCreated())
      .andExpect(content().json(expectedJson))
      .andExpect(header().string("Location", DEVICES_API + "/1"));
  }

  @Test
  @SneakyThrows
  void shouldReturnNotFoundIfDeviceWithIdDoesNotExist() {
    when(devicesApplicationService.getDeviceById(1L))
      .thenThrow(new NotFoundException("Device with id not found"));

    MockHttpServletRequestBuilder request = get(DEVICES_API + "/1");

    mockMvc.perform(request)
      .andExpect(status().isNotFound());
  }

  @Test
  @SneakyThrows
  void shouldFindDeviceById() {
    List<DeviceParameterDto> parameterDtos = List.of(
      new DeviceParameterDto(11, 1, "Param A"),
      new DeviceParameterDto(12, 2, "Param B"));
    DeviceDetailsDto dto = new DeviceDetailsDto(1L, "d1", DeviceState.NEW,
      ZonedDateTime.parse("2022-07-24T16:00:00Z"), parameterDtos);
    when(devicesApplicationService.getDeviceById(1)).thenReturn(dto);

    MockHttpServletRequestBuilder request = get(DEVICES_API + "/1");

    String expectedJson = "{\"id\": 1, \"name\": \"d1\", \"state\": \"NEW\", \"createdAt\": \"2022-07-24T16:00:00Z\"," +
      "\"parameters\": [" +
      "{\"id\": 11, \"anchor\": 1, \"name\": \"Param A\"}," +
      "{\"id\": 12, \"anchor\": 2, \"name\": \"Param B\"}" +
      "]}";
    mockMvc.perform(request)
      .andExpect(status().isOk())
      .andExpect(content().json(expectedJson, true));
  }

  @Test
  @SneakyThrows
  void shouldChannelIdForDevice() {
    when(devicesApplicationService.getChannelIdForDevice(2L)).thenReturn(new ChannelIdDto("exampleChannelId-123"));

    MockHttpServletRequestBuilder request = get(DEVICES_API + "/2/channelId");

    String expectedJson = "{\"channelId\": \"exampleChannelId-123\"}";
    mockMvc.perform(request)
      .andExpect(status().isOk())
      .andExpect(content().json(expectedJson));
  }

  @Test
  @SneakyThrows
  void shouldReturnNotFoundIfChannelIdDoesNotExist() {
    when(devicesApplicationService.getChannelIdForDevice(2L))
      .thenThrow(new NotFoundException("Device with id not found"));

    MockHttpServletRequestBuilder request = get(DEVICES_API + "/2/channelId");

    mockMvc.perform(request)
      .andExpect(status().isNotFound());
  }
}
