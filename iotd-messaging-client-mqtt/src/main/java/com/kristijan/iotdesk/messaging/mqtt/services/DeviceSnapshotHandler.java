package com.kristijan.iotdesk.messaging.mqtt.services;

import com.kristijan.iotdesk.domain.snapshots.models.AnchorSnapshot;
import com.kristijan.iotdesk.domain.snapshots.models.DeviceSnapshot;
import com.kristijan.iotdesk.domain.snapshots.services.AddDeviceSnapshotService;
import com.kristijan.iotdesk.domain.snapshots.services.DeviceMessagingErrorHandler;
import com.kristijan.iotdesk.messaging.mqtt.models.ParsingResult;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DeviceSnapshotHandler {

  private static final String TOPIC_FILTER = "devices/+/snapshots";

  private final DeviceMessagingErrorHandler deviceMessagingErrorHandler;
  private final MqttPayloadValidatorAndParser mqttPayloadValidatorAndParser;
  private final AddDeviceSnapshotService addDeviceSnapshotService;
  private final Clock clock;

  public DeviceSnapshotHandler(DeviceMessagingErrorHandler deviceMessagingErrorHandler,
                               MqttPayloadValidatorAndParser mqttPayloadValidatorAndParser,
                               AddDeviceSnapshotService addDeviceSnapshotService, Clock clock, MqttClient mqttClient) {
    this.deviceMessagingErrorHandler = deviceMessagingErrorHandler;
    this.mqttPayloadValidatorAndParser = mqttPayloadValidatorAndParser;
    this.addDeviceSnapshotService = addDeviceSnapshotService;
    this.clock = clock;
    try {
      mqttClient.subscribe(TOPIC_FILTER, this::handleSnapshotMessage);
    } catch (MqttException e) {
      throw new RuntimeException("Failed to subscribe to topic: " + TOPIC_FILTER, e);
    }
  }

  /**
   * This method handles a snapshot message from a device. It persists the parameter snapshots.
   *
   * @param topic the topic name where the message was received.
   * @param message the mqtt message payload.
   * @return true if snapshot was handled successfully.
   */
  public boolean handleSnapshotMessage(String topic, MqttMessage message) {
    String channelId = topic.split("/")[1];
    return handleSnapshot(channelId, message.getPayload());
  }

  private boolean handleSnapshot(String channelId, byte[] payload) {
    ParsingResult parsingResult = mqttPayloadValidatorAndParser.parsePayload(payload);
    if (!parsingResult.isValid()) {
      deviceMessagingErrorHandler.invalidPayload(channelId);
      return false;
    }

    List<AnchorSnapshot> anchorSnapshots = parsingResult.getAnchorSnapshots();
    DeviceSnapshot deviceSnapshot = new DeviceSnapshot(channelId, LocalDateTime.now(clock), anchorSnapshots);
    try {
      return addDeviceSnapshotService.addDeviceSnapshot(deviceSnapshot);
    } catch (Exception ex) {
      deviceMessagingErrorHandler.handleDomainException(channelId, ex);
      return false;
    }
  }

}
