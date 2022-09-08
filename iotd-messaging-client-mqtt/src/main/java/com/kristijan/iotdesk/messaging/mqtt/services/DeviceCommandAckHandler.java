package com.kristijan.iotdesk.messaging.mqtt.services;

import com.kristijan.iotdesk.domain.snapshots.models.CommandAck;
import com.kristijan.iotdesk.domain.snapshots.services.DeviceCommandAckService;
import com.kristijan.iotdesk.domain.snapshots.services.DeviceMessagingErrorHandler;
import com.kristijan.iotdesk.messaging.mqtt.models.ParsingResult;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceCommandAckHandler implements MqttMessageHandler {

  private static final String ACKS_TOPIC_FILTER = "devices/+/acks";

  private final MqttCommandAckValidatorAndParser mqttCommandAckValidatorAndParser;
  private final DeviceMessagingErrorHandler deviceMessagingErrorHandler;
  private final DeviceCommandAckService deviceCommandAckService;

  @Override
  public String getTopicFilter() {
    return ACKS_TOPIC_FILTER;
  }

  @Override
  public void handleMqttMessage(String topic, MqttMessage message) {
    String channelId = topic.split("/")[1];
    ParsingResult<CommandAck> parsingResult = mqttCommandAckValidatorAndParser.parsePayload(message.getPayload());
    if (!parsingResult.isValid()) {
      deviceMessagingErrorHandler.invalidCommandAckPayload(channelId);
    }
    try {
      deviceCommandAckService.acknowledgeCommand(channelId, parsingResult.getResult());
    } catch (Exception ex) {
      deviceMessagingErrorHandler.handleCommandAckDomainException(channelId, ex);
    }
  }
}
