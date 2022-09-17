package com.kristijan.iotdesk.e2e;


import com.kristijan.iotdesk.application.dtos.DeviceDetailsDto;
import com.kristijan.iotdesk.application.dtos.DeviceParameterDto;
import com.kristijan.iotdesk.application.dtos.ParameterSnapshotDto;
import com.kristijan.iotdesk.e2e.config.E2EConfiguration;
import com.kristijan.iotdesk.e2e.services.IotDeskServerApi;
import com.kristijan.iotdesk.e2e.services.MqttApi;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = E2EConfiguration.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class DeviceRegistrationE2eTest {

  @Autowired
  private IotDeskServerApi serverApi;

  @Autowired
  private MqttApi mqttApi;

  @Test
  @SneakyThrows
  void testRegistrationOfNewDevice() {
    Long deviceId = serverApi.createNewDevice();
    String channelId = serverApi.getChannelId(deviceId);

    mqttApi.publishDeviceSnapshot(channelId, "1:1.3,2:5.5");

    // Check device details after device mqtt message, verify that parameters are present.
    DeviceDetailsDto device = serverApi.getDeviceDetails(deviceId);
    assertEquals(2, device.getParameters().size());
    Map<Integer, Long> parameterByAnchor = device.getParameters().stream()
      .collect(Collectors.toMap(DeviceParameterDto::getAnchor, DeviceParameterDto::getId));
    Long idForAnchor1 = parameterByAnchor.get(1);
    assertNotNull(idForAnchor1);
    Long idForAnchor2 = parameterByAnchor.get(2);
    assertNotNull(idForAnchor2);

    // Verify that parameter snapshots are persisted.
    Map<String, String> urlParams = Map.of(
      "beginRange", Instant.now().minus(1, ChronoUnit.HOURS).toString(),
      "endRange", Instant.now().plus(1, ChronoUnit.HOURS).toString());
    List<ParameterSnapshotDto> snapshots1 = serverApi.getParameterSnapshots(idForAnchor1, urlParams);
    assertEquals(1, snapshots1.size());
    assertEquals(1.3, snapshots1.get(0).getValue());

    List<ParameterSnapshotDto> snapshots2 = serverApi.getParameterSnapshots(idForAnchor2, urlParams);
    assertEquals(1, snapshots2.size());
    assertEquals(5.5, snapshots2.get(0).getValue());
  }

  @Test
  void editParameterName() {
    Long deviceId = serverApi.createNewDevice();
    String channelId = serverApi.getChannelId(deviceId);

    mqttApi.publishDeviceSnapshot(channelId, "1:1.4,3:44.5");

    DeviceDetailsDto device = serverApi.getDeviceDetails(deviceId);
    assertEquals(2, device.getParameters().size());
    DeviceParameterDto parameter3 = device.getParameters().stream()
      .filter(param -> param.getAnchor() == 3)
      .findFirst()
      .orElse(null);
    assertNotNull(parameter3);
    assertEquals(3, parameter3.getAnchor());
    assertEquals("Parameter 3", parameter3.getName());

    // Rename parameter
    serverApi.renameParameter(deviceId, 3, "Temperature");

    // Reload parameter
    device = serverApi.getDeviceDetails(deviceId);
    assertEquals(2, device.getParameters().size());
    parameter3 = device.getParameters().stream()
      .filter(param -> param.getAnchor() == 3)
      .findFirst()
      .orElse(null);
    assertNotNull(parameter3);
    assertEquals(3, parameter3.getAnchor());
    assertEquals("Temperature", parameter3.getName());
  }
}
