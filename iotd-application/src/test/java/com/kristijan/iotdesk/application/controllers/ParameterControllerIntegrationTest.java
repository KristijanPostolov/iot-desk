package com.kristijan.iotdesk.application.controllers;

import com.kristijan.iotdesk.application.dtos.ParameterSnapshotDto;
import com.kristijan.iotdesk.application.services.SnapshotsApplicationService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ParameterController.class)
public class ParameterControllerIntegrationTest {

  public static final String PARAMETERS_API = "/api/v1/parameters";

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private SnapshotsApplicationService snapshotsApplicationService;

  @Test
  @SneakyThrows
  void shouldReturnBadRequestWhenTimeRangeIsNotProvided() {
    MockHttpServletRequestBuilder request = get(PARAMETERS_API + "/1/snapshots");

    mockMvc.perform(request)
      .andExpect(status().isBadRequest());
  }

  @Test
  @SneakyThrows
  void shouldReturnListOfParameterSnapshots() {
    ZonedDateTime beginRange = ZonedDateTime.parse("2022-08-16T21:00:00Z");
    ZonedDateTime endRange = ZonedDateTime.parse("2022-08-16T22:00:00Z");
    when(snapshotsApplicationService.getParameterSnapshots(1L, beginRange, endRange))
      .thenReturn(List.of(
        new ParameterSnapshotDto(beginRange.plusMinutes(1), 1.2),
        new ParameterSnapshotDto(beginRange.plusMinutes(2), 3)));

    MockHttpServletRequestBuilder request = get(PARAMETERS_API + "/1/snapshots")
      .param("beginRange", beginRange.format(DateTimeFormatter.ISO_DATE_TIME))
      .param("endRange", endRange.format(DateTimeFormatter.ISO_DATE_TIME));

    mockMvc.perform(request)
      .andExpect(status().isOk())
      .andExpect(content().json("[" +
        "{\"timestamp\": \"2022-08-16T21:01:00Z\", \"value\": 1.2}, " +
        "{\"timestamp\": \"2022-08-16T21:02:00Z\", \"value\": 3.0}" +
        "]", true));
  }
}
