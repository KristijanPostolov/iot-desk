package com.kristijan.iotdesk.domain.snapshots.services;

import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceChannelId;
import com.kristijan.iotdesk.domain.device.models.DeviceState;
import com.kristijan.iotdesk.domain.device.services.ChannelIdService;
import com.kristijan.iotdesk.domain.device.services.ListDevicesService;
import com.kristijan.iotdesk.domain.device.services.ManageDevicesService;
import com.kristijan.iotdesk.domain.snapshots.models.AnchorSnapshot;
import com.kristijan.iotdesk.domain.snapshots.models.DeviceSnapshot;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AddDeviceSnapshotService {

  private final DeviceMessagingErrorHandler deviceMessagingErrorHandler;
  private final ChannelIdService channelIdService;
  private final ListDevicesService listDevicesService;
  private final ManageDevicesService manageDevicesService;

  public boolean addDeviceSnapshot(DeviceSnapshot deviceSnapshot) {
    String channelId = deviceSnapshot.getChannelId();
    Optional<DeviceChannelId> deviceChannelIdOptional = channelIdService.findByChannelId(channelId);
    if (deviceChannelIdOptional.isEmpty()) {
      deviceMessagingErrorHandler.nonExistingChannelId(channelId);
      return false;
    }
    long deviceId = deviceChannelIdOptional.get().getDeviceId();

    Optional<Device> deviceOptional = listDevicesService.findById(deviceId);
    if (deviceOptional.isEmpty()) {
      deviceMessagingErrorHandler.nonExistingDevice(deviceId);
      return false;
    }
    if (DeviceState.NEW.equals(deviceOptional.get().getState())) {
      manageDevicesService.activateDevice(deviceId);
    }
    Device device = manageDevicesService.updateDeviceParameters(deviceId, getAnchorsFromSnapshot(deviceSnapshot));

    // TODO: Persist measurements.
    return true;
  }

  private Set<Integer> getAnchorsFromSnapshot(DeviceSnapshot deviceSnapshot) {
    return deviceSnapshot.getAnchorSnapshots().stream()
      .map(AnchorSnapshot::getAnchor)
      .collect(Collectors.toSet());
  }


}
