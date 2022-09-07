package com.kristijan.iotdesk.messaging.mqtt.services;

import com.kristijan.iotdesk.domain.snapshots.models.CommandData;
import com.kristijan.iotdesk.domain.snapshots.ports.DeviceCommandSender;
import com.kristijan.iotdesk.messaging.mqtt.exceptions.MqttRuntimeException;
import lombok.RequiredArgsConstructor;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MqttDeviceCommandSender implements DeviceCommandSender {

  public static final String COMMAND_ID_SEPARATOR = ";";
  private final MqttClient mqttClient;

  @Override
  public void sendCommandToDevice(String channelId, CommandData commandData) {
    String payload = commandData.getCommandId() + COMMAND_ID_SEPARATOR + commandData.getContent();
    MqttMessage mqttMessage = new MqttMessage(payload.getBytes(StandardCharsets.UTF_8));
    try {
      mqttClient.publish(getTopic(channelId), mqttMessage);
    } catch (MqttException e) {
      throw new MqttRuntimeException("Exception occurred while publishing a mqtt message", e);
    }
  }

  private String getTopic(String channelId) {
    return "/devices/" + channelId + "/commands";
  }
}
