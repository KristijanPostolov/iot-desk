package com.kristijan.iotdesk.integration.tests.cases;

import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceChannelId;
import com.kristijan.iotdesk.domain.device.models.DeviceParameter;
import com.kristijan.iotdesk.domain.device.models.DeviceState;
import com.kristijan.iotdesk.domain.device.services.ChannelIdService;
import com.kristijan.iotdesk.domain.device.services.ListDevicesService;
import com.kristijan.iotdesk.domain.device.services.ManageDevicesService;
import com.kristijan.iotdesk.domain.snapshots.models.AnchorSnapshot;
import com.kristijan.iotdesk.domain.snapshots.models.DeviceSnapshot;
import com.kristijan.iotdesk.domain.snapshots.models.ParameterSnapshot;
import com.kristijan.iotdesk.domain.snapshots.services.AddDeviceSnapshotService;
import com.kristijan.iotdesk.integration.tests.IntegrationTestConfiguration;
import com.kristijan.iotdesk.persistence.mock.repositories.DevicesRepositoryMock;
import com.kristijan.iotdesk.persistence.mock.repositories.ParameterSnapshotRepositoryMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(classes = IntegrationTestConfiguration.class)
public class SnapshotUseCaseIntegrationTest {

  @Autowired
  private AddDeviceSnapshotService addDeviceSnapshotService;

  @Autowired
  private ManageDevicesService manageDevicesService;

  @Autowired
  private ListDevicesService listDevicesService;

  @Autowired
  private ChannelIdService channelIdService;

  @Autowired
  private DevicesRepositoryMock devicesRepository;

  @Autowired
  private ParameterSnapshotRepositoryMock parameterSnapshotRepository;

  @Autowired
  private Clock clock;

  @AfterEach
  void tearDown() {
    devicesRepository.reset();
    parameterSnapshotRepository.reset();
  }

  @Test
  void shouldSetupDeviceParametersAndSaveSnapshots() {
    long deviceId = manageDevicesService.createNewDevice("Device 1");
    String channelId = channelIdService.findByDeviceId(deviceId).map(DeviceChannelId::getChannelId).orElse(null);

    DeviceSnapshot snapshot = new DeviceSnapshot(channelId, LocalDateTime.now(clock),
      List.of(new AnchorSnapshot(1, 5.5), new AnchorSnapshot(2, 3.56)));
    addDeviceSnapshotService.addDeviceSnapshot(snapshot);

    // Check if the 2 parameters are created
    Device device = listDevicesService.findById(deviceId).orElse(null);
    assertNotNull(device);
    assertEquals(DeviceState.ACTIVE, device.getState());
    Set<DeviceParameter> parameters = device.getParameters();
    assertEquals(2, parameters.size());

    // check that value for anchor 1 was saved
    long deviceParam1 = getParameterIdByAnchor(parameters, 1);
    List<ParameterSnapshot> parameterSnapshots1 = parameterSnapshotRepository.getByParameterId(deviceParam1);
    assertEquals(1, parameterSnapshots1.size());
    assertEquals(5.5, parameterSnapshots1.get(0).getValue());

    // check that value for anchor 2 was saved
    long deviceParam2 = getParameterIdByAnchor(parameters, 2);
    List<ParameterSnapshot> parameterSnapshots2 = parameterSnapshotRepository.getByParameterId(deviceParam2);
    assertEquals(1, parameterSnapshots2.size());
    assertEquals(3.56, parameterSnapshots2.get(0).getValue());

    assertNotEquals(parameterSnapshots1.get(0).getId(), parameterSnapshots2.get(0).getId());
  }

  private long getParameterIdByAnchor(Set<DeviceParameter> parameters, int anchor) {
    return parameters.stream()
      .filter(parameter -> parameter.getAnchor() == anchor)
      .map(DeviceParameter::getId)
      .findFirst().orElse(-1L);
  }
}
