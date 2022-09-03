package com.kristijan.iotdesk.e2e.services;

import com.kristijan.iotdesk.application.dtos.ChannelIdDto;
import com.kristijan.iotdesk.application.dtos.CreateDeviceDto;
import com.kristijan.iotdesk.application.dtos.DeviceDetailsDto;
import com.kristijan.iotdesk.application.dtos.ParameterSnapshotDto;
import com.kristijan.iotdesk.e2e.config.E2ETestProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Service
@RequiredArgsConstructor
public class IotDeskServerApi {

  private static final String API_PREFIX = "/api/v1";

  private final RestTemplate restTemplate;
  private final E2ETestProperties properties;

  public List<ParameterSnapshotDto> getParameterSnapshots(Long parameterId, Map<String, String> urlParams) {
    ResponseEntity<ParameterSnapshotDto[]> response = restTemplate.getForEntity(
      url("/parameters/" + parameterId + "/snapshots?beginRange={beginRange}&endRange={endRange}"),
      ParameterSnapshotDto[].class, urlParams);
    assertNotNull(response.getBody());
    return Arrays.asList(response.getBody());
  }

  public DeviceDetailsDto getDeviceDetails(Long deviceId) {
    DeviceDetailsDto device = restTemplate.getForObject(url("/devices/" + deviceId), DeviceDetailsDto.class);
    assertNotNull(device);
    return device;
  }

  public String getChannelId(Long deviceId) {
    ChannelIdDto channelIdDto = restTemplate.getForObject(url( "/devices/" + deviceId + "/channelId"),
      ChannelIdDto.class);
    assertNotNull(channelIdDto);
    return channelIdDto.getChannelId();
  }

  public Long createNewDevice() {
    String deviceName = "Test_" + UUID.randomUUID();
    ResponseEntity<Void> newDeviceResponse = restTemplate.postForEntity(url("/devices"),
      new CreateDeviceDto(deviceName), Void.class);
    String location = newDeviceResponse.getHeaders().getOrEmpty(HttpHeaders.LOCATION).get(0);
    String[] parts = location.split("/");
    return Long.parseLong(parts[parts.length - 1]);
  }

  private String url(String path) {
    return properties.getServerUrl() + API_PREFIX + path;
  }


}
