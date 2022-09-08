package com.kristijan.iotdesk.messaging.mqtt.services;

import com.kristijan.iotdesk.domain.snapshots.models.CommandAck;
import com.kristijan.iotdesk.domain.snapshots.services.DeviceCommandAckService;
import com.kristijan.iotdesk.messaging.mqtt.MqttContainerTest;
import com.kristijan.iotdesk.messaging.mqtt.MqttIntegrationTestConfig;
import lombok.SneakyThrows;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {MqttIntegrationTestConfig.class})
class DeviceCommandAckHandlerIntegrationTest extends MqttContainerTest {

  @MockBean
  private DeviceCommandAckService deviceCommandAckService;

  @Autowired
  private MqttClient mqttClient;

  private final String channelId = UUID.randomUUID().toString();
  private final String commandId = UUID.randomUUID().toString();

  @Test
  @SneakyThrows
  void shouldHandleDeviceCommandAcknowledgement() {
    CommandAck commandAck = new CommandAck(commandId, true);
    when(deviceCommandAckService.acknowledgeCommand(channelId, commandAck)).thenReturn(true);

    String message = commandId + ";1";
    mqttClient.publish("devices/" + channelId + "/acks",
      new MqttMessage(message.getBytes(StandardCharsets.UTF_8)));

    verify(deviceCommandAckService).acknowledgeCommand(channelId, commandAck);
  }

  @Test
  @SneakyThrows
  void shouldNotHandleMessagesFromDifferentTopics() {
    String message = commandId + ";1";
    mqttClient.publish("devices", new MqttMessage(message.getBytes(StandardCharsets.UTF_8)));

    verify(deviceCommandAckService, times(0)).acknowledgeCommand(any(), any());
  }
}