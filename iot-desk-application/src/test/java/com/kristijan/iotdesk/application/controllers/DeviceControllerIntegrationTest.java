package com.kristijan.iotdesk.application.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kristijan.iotdesk.application.dtos.CreateDeviceDto;
import com.kristijan.iotdesk.application.dtos.DeviceDto;
import com.kristijan.iotdesk.application.services.DevicesApplicationService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
    when(devicesApplicationService.getAllDevices())
      .thenReturn(List.of(new DeviceDto("d1"), new DeviceDto("d2")));
    MockHttpServletRequestBuilder request = get(DEVICES_API);

    mockMvc.perform(request)
      .andExpect(status().isOk())
      .andExpect(content().json("[{\"name\": \"d1\"},{\"name\": \"d2\"}]"));
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

  @SneakyThrows
  private String asJsonString(Object obj) {
    return objectMapper.writeValueAsString(obj);
  }
}
