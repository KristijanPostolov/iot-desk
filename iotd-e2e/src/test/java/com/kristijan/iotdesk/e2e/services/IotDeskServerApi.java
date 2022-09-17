package com.kristijan.iotdesk.e2e.services;

import com.kristijan.iotdesk.application.dtos.ChannelIdDto;
import com.kristijan.iotdesk.application.dtos.CreateCommandDto;
import com.kristijan.iotdesk.application.dtos.CreateDeviceDto;
import com.kristijan.iotdesk.application.dtos.CreateDeviceResponseDto;
import com.kristijan.iotdesk.application.dtos.DeviceCommandDto;
import com.kristijan.iotdesk.application.dtos.DeviceDetailsDto;
import com.kristijan.iotdesk.application.dtos.ParameterRenameDto;
import com.kristijan.iotdesk.application.dtos.ParameterSnapshotDto;
import com.kristijan.iotdesk.e2e.config.E2ETestProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    CreateDeviceResponseDto newDeviceResponse = restTemplate.postForObject(url("/devices"),
      new CreateDeviceDto(deviceName), CreateDeviceResponseDto.class);
    assertNotNull(newDeviceResponse);
    return newDeviceResponse.getId();
  }

  public void sendCommand(Long deviceId, String command) {
    CreateCommandDto request = new CreateCommandDto(deviceId, command);
    ResponseEntity<Void> response = restTemplate.postForEntity(url("/commands"), request, Void.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  public List<DeviceCommandDto> getCommands(long deviceId, Instant beginRange, Instant endRange) {
    Map<String, String> params = Map.of(
      "deviceId", String.valueOf(deviceId),
      "beginRange", beginRange.toString(),
      "endRange", endRange.toString());
    ResponseEntity<DeviceCommandDto[]> result = restTemplate.getForEntity(
      url("/commands?deviceId={deviceId}&beginRange={beginRange}&endRange={endRange}"),
      DeviceCommandDto[].class, params);
    assertNotNull(result.getBody());
    return Arrays.asList(result.getBody());
  }

  public void renameParameter(long deviceId, int anchor, String name) {
    ResponseEntity<Void> response = restTemplate.postForEntity(
      url("/devices/" + deviceId + "/parameters/" + anchor + "/rename"),
      new ParameterRenameDto(name), Void.class);
    assertEquals(HttpStatus.OK, response.getStatusCode());
  }

  private String url(String path) {
    return properties.getServerUrl() + API_PREFIX + path;
  }


}
