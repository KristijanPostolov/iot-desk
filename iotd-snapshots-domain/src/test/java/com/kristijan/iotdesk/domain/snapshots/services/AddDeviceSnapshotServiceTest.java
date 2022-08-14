package com.kristijan.iotdesk.domain.snapshots.services;

import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceChannelId;
import com.kristijan.iotdesk.domain.device.models.DeviceState;
import com.kristijan.iotdesk.domain.device.services.ChannelIdService;
import com.kristijan.iotdesk.domain.device.services.ListDevicesService;
import com.kristijan.iotdesk.domain.device.services.ManageDevicesService;
import com.kristijan.iotdesk.domain.snapshots.models.AnchorSnapshot;
import com.kristijan.iotdesk.domain.snapshots.models.DeviceSnapshot;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
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

    List<AnchorSnapshot> anchorSnapshots =
      List.of(new AnchorSnapshot(1, 2), new AnchorSnapshot(3,3.4));
    boolean result = addDeviceSnapshotService.addDeviceSnapshot(new DeviceSnapshot("channelId2", null, anchorSnapshots));

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

    List<AnchorSnapshot> anchorSnapshots =
      List.of(new AnchorSnapshot(1, 2), new AnchorSnapshot(3,3.4));
    boolean result = addDeviceSnapshotService.addDeviceSnapshot(new DeviceSnapshot("channelId2", null, anchorSnapshots));

    assertTrue(result);
    verify(manageDevicesService, times(0)).activateDevice(anyLong());
    verify(manageDevicesService).updateDeviceParameters(2L, Set.of(1, 3));
  }

  private Device createDevice(long id, String name, DeviceState state) {
    Device device = new Device(name, state);
    device.setId(id);
    return device;
  }


}