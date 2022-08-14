package com.kristijan.iotdesk.messaging.mqtt.services;

import com.kristijan.iotdesk.domain.snapshots.models.AnchorSnapshot;
import com.kristijan.iotdesk.domain.snapshots.models.DeviceSnapshot;
import com.kristijan.iotdesk.domain.snapshots.services.AddDeviceSnapshotService;
import com.kristijan.iotdesk.domain.snapshots.services.DeviceMessagingErrorHandler;
import com.kristijan.iotdesk.messaging.mqtt.models.MappingResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceSnapshotHandler {

  private final DeviceMessagingErrorHandler deviceMessagingErrorHandler;
  private final MqttPayloadValidatorAndMapper mqttPayloadValidatorAndMapper;
  private final AddDeviceSnapshotService addDeviceSnapshotService;
  private final Clock clock;

  /**
   * This method handles a snapshot from a device. It persists the parameter snapshots.
   *
   * @param channelId the channelId where the snapshot was received.
   * @param payload the mqtt message payload.
   * @return true if snapshot was handled successfully.
   */
  public boolean handleSnapshot(String channelId, byte[] payload) {
    MappingResult mappingResult = mqttPayloadValidatorAndMapper.mapPayload(payload);
    if (!mappingResult.isValid()) {
      deviceMessagingErrorHandler.invalidPayload(channelId);
      return false;
    }

    List<AnchorSnapshot> anchorSnapshots = mappingResult.getAnchorSnapshots();
    DeviceSnapshot deviceSnapshot = new DeviceSnapshot(channelId, LocalDateTime.now(clock), anchorSnapshots);
    return addDeviceSnapshotService.addDeviceSnapshot(deviceSnapshot);
  }

}
