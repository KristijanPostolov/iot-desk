package com.kristijan.iotdesk.messaging.mqtt.services;

import com.kristijan.iotdesk.domain.snapshots.models.AnchorSnapshot;
import com.kristijan.iotdesk.domain.snapshots.models.DeviceSnapshot;
import com.kristijan.iotdesk.domain.snapshots.services.AddDeviceSnapshotService;
import com.kristijan.iotdesk.domain.snapshots.services.DeviceMessagingErrorHandler;
import com.kristijan.iotdesk.messaging.mqtt.models.ParsingResult;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

/**
 * This class handles snapshot messages from devices. It persists the parameter snapshots.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class DeviceSnapshotHandler implements MqttMessageHandler {

  private static final String SNAPSHOTS_TOPIC_FILTER = "devices/+/snapshots";

  private final DeviceMessagingErrorHandler deviceMessagingErrorHandler;
  private final MqttPayloadValidatorAndParser mqttPayloadValidatorAndParser;
  private final AddDeviceSnapshotService addDeviceSnapshotService;
  private final Clock clock;

  @Override
  public String getTopicFilter() {
    return SNAPSHOTS_TOPIC_FILTER;
  }

  @Override
  public void handleMqttMessage(String topic, MqttMessage message) {
    String channelId = topic.split("/")[1];
    handleSnapshot(channelId, message.getPayload());
  }

  private void handleSnapshot(String channelId, byte[] payload) {
    ParsingResult parsingResult = mqttPayloadValidatorAndParser.parsePayload(payload);
    if (!parsingResult.isValid()) {
      deviceMessagingErrorHandler.invalidPayload(channelId);
      return;
    }

    List<AnchorSnapshot> anchorSnapshots = parsingResult.getAnchorSnapshots();
    DeviceSnapshot deviceSnapshot = new DeviceSnapshot(channelId, LocalDateTime.now(clock), anchorSnapshots);
    try {
      addDeviceSnapshotService.addDeviceSnapshot(deviceSnapshot);
    } catch (Exception ex) {
      deviceMessagingErrorHandler.handleDomainException(channelId, ex);
    }
  }
}
