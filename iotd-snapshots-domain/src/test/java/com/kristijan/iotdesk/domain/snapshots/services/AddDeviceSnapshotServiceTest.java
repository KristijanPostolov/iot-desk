package com.kristijan.iotdesk.domain.snapshots.services;

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
import com.kristijan.iotdesk.domain.snapshots.repositories.ParameterSnapshotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AddDeviceSnapshotServiceTest {

  @InjectMocks
  private AddDeviceSnapshotService addDeviceSnapshotService;

  @Mock
  private DeviceMessagingErrorHandler deviceMessagingErrorHandler;

  @Mock
  private ChannelIdService channelIdService;

  @Mock
  private ListDevicesService listDevicesService;

  @Mock
  private ManageDevicesService manageDevicesService;

  @Mock
  private ParameterSnapshotRepository parameterSnapshotRepository;

  private final LocalDateTime now = LocalDateTime.parse("2022-08-15T21:20:01");

  @Test
  void shouldHandleErrorForNonExistingChannelId() {
    boolean result = addDeviceSnapshotService.addDeviceSnapshot(new DeviceSnapshot("channelId1", null, null));

    assertFalse(result);
    verify(deviceMessagingErrorHandler).nonExistingChannelId("channelId1");
  }

  @Test
  void shouldHandleErrorForNonExistingDevice() {
    when(channelIdService.findByChannelId("channelId1"))
      .thenReturn(Optional.of(new DeviceChannelId(1L, "channelId1")));

    boolean result = addDeviceSnapshotService.addDeviceSnapshot(new DeviceSnapshot("channelId1", null, null));

    assertFalse(result);
    verify(deviceMessagingErrorHandler).nonExistingDevice(1L);
  }

  @Test
  void shouldUpdateDeviceStateAndParametersOnFirstSnapshot() {
    when(channelIdService.findByChannelId("channelId2"))
      .thenReturn(Optional.of(new DeviceChannelId(2L, "channelId2")));
    Device device = createDevice(2L, "device2", DeviceState.NEW);
    when(listDevicesService.findById(2L)).thenReturn(Optional.of(device));
    when(manageDevicesService.updateDeviceParameters(eq(2L), any()))
      .thenReturn(getUpdatedDevice(device, 1, 3));

    List<AnchorSnapshot> anchorSnapshots =
      List.of(new AnchorSnapshot(1, 2), new AnchorSnapshot(3,3.4));
    boolean result = addDeviceSnapshotService.addDeviceSnapshot(
      new DeviceSnapshot("channelId2", now, anchorSnapshots));

    assertTrue(result);
    verify(manageDevicesService).activateDevice(2L);
    verify(manageDevicesService).updateDeviceParameters(2L, Set.of(1, 3));
  }

  @Test
  void shouldUpdateParametersButNotStateOnAlreadyActiveDevice() {
    when(channelIdService.findByChannelId("channelId2"))
      .thenReturn(Optional.of(new DeviceChannelId(2L, "channelId2")));
    Device device = createDevice(2L, "device2", DeviceState.ACTIVE);
    when(listDevicesService.findById(2L)).thenReturn(Optional.of(device));
    when(manageDevicesService.updateDeviceParameters(eq(2L), any()))
      .thenReturn(getUpdatedDevice(device, 1, 3));

    List<AnchorSnapshot> anchorSnapshots =
      List.of(new AnchorSnapshot(1, 2), new AnchorSnapshot(3,3.4));
    boolean result = addDeviceSnapshotService.addDeviceSnapshot(
      new DeviceSnapshot("channelId2", now, anchorSnapshots));

    assertTrue(result);
    verify(manageDevicesService, times(0)).activateDevice(anyLong());
    verify(manageDevicesService).updateDeviceParameters(2L, Set.of(1, 3));
  }

  @Test
  void shouldSaveParameterSnapshots() {
    when(channelIdService.findByChannelId("channelId2"))
      .thenReturn(Optional.of(new DeviceChannelId(2L, "channelId2")));
    Device device = createDevice(2L, "device2", DeviceState.ACTIVE);
    device.getParameters().add(createDeviceParameter(10, 1));
    device.getParameters().add(createDeviceParameter(11, 3));
    device.getParameters().add(createDeviceParameter(13, 5));
    when(listDevicesService.findById(2L)).thenReturn(Optional.of(device));
    when(manageDevicesService.updateDeviceParameters(anyLong(), any())).thenReturn(device);

    DeviceSnapshot snapshot = new DeviceSnapshot("channelId2", now,
      List.of(new AnchorSnapshot(1, 2), new AnchorSnapshot(3,3.4)));
    boolean result = addDeviceSnapshotService.addDeviceSnapshot(snapshot);

    assertTrue(result);
    ArgumentCaptor<List<ParameterSnapshot>> parameterSnapshotsCaptor = ArgumentCaptor.forClass(List.class);
    verify(parameterSnapshotRepository).saveParameterSnapshots(parameterSnapshotsCaptor.capture());
    List<ParameterSnapshot> parameterSnapshots = parameterSnapshotsCaptor.getValue();
    assertEquals(2, parameterSnapshots.size());
    assertParameterSnapshot(parameterSnapshots.get(0), 10, now, 2);
    assertParameterSnapshot(parameterSnapshots.get(1), 11, now, 3.4);
  }

  private Device createDevice(long id, String name, DeviceState state) {
    Device device = new Device(name, state);
    device.setId(id);
    return device;
  }

  private Device getUpdatedDevice(Device device, Integer... anchors) {
    Device updated = createDevice(device.getId(), device.getName(), DeviceState.ACTIVE);
    Arrays.stream(anchors).forEach(anchor ->
      updated.getParameters().add(createDeviceParameter(anchor, anchor))
    );
    return updated;
  }

  private DeviceParameter createDeviceParameter(long id, int anchor) {
    DeviceParameter deviceParameter = new DeviceParameter(2L, anchor, "Param " + anchor);
    deviceParameter.setId(id);
    return deviceParameter;
  }

  private void assertParameterSnapshot(ParameterSnapshot snapshot, long expectedParameterId,
                                       LocalDateTime expectedTimestamp, double expectedValue) {
    assertNotNull(snapshot);
    assertEquals(expectedParameterId, snapshot.getParameterId());
    assertEquals(expectedTimestamp, snapshot.getTimestamp());
    assertEquals(expectedValue, snapshot.getValue());
  }


}