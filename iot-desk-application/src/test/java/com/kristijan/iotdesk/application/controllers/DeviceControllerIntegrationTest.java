package com.kristijan.iotdesk.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kristijan.iotdesk.application.dtos.CreateDeviceDto;
import com.kristijan.iotdesk.application.dtos.DeviceDetailsDto;
import com.kristijan.iotdesk.application.dtos.DeviceDto;
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

@WebMvcTest
public class DeviceControllerIntegrationTest {

  public static final String DEVICES_API = "/api/v1/devices";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private DevicesApplicationService devicesApplicationService;

  @Autowired
  private ObjectMapper objectMapper;

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
    CreateDeviceDto dto = new CreateDeviceDto("New Device");
    when(devicesApplicationService.createNewDevice(any())).thenReturn(1L);

    MockHttpServletRequestBuilder request = post(DEVICES_API)
      .content(asJsonString(dto))
      .contentType(MediaType.APPLICATION_JSON);

    mockMvc.perform(request)
      .andExpect(status().isCreated())
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
    when(devicesApplicationService.getDeviceById(1)).thenReturn(
      new DeviceDetailsDto(1L, "d1", DeviceState.NEW, ZonedDateTime.parse("2022-07-24T16:00:00Z")));

    MockHttpServletRequestBuilder request = get(DEVICES_API + "/1");

    String expectedJson = "{\"id\": 1, \"name\": \"d1\", \"state\": \"NEW\", \"createdAt\": \"2022-07-24T16:00:00Z\"}";
    mockMvc.perform(request)
      .andExpect(status().isOk())
      .andExpect(content().json(expectedJson));
  }

  @SneakyThrows
  private String asJsonString(Object obj) {
    return objectMapper.writeValueAsString(obj);
  }
}
