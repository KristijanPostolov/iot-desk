package com.kristijan.iotdesk.messaging.mqtt.services;

import com.kristijan.iotdesk.domain.snapshots.models.CommandData;
import com.kristijan.iotdesk.messaging.mqtt.exceptions.MqttRuntimeException;
import lombok.SneakyThrows;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MqttDeviceCommandSenderTest {

  @InjectMocks
  private MqttDeviceCommandSender mqttDeviceCommandSender;

  @Mock
  private MqttClient mqttClient;

  @Test
  @SneakyThrows
  void shouldThrowExceptionWhenSendingFails() {
    CommandData commandData = new CommandData("commandId", "commandContent");
    doThrow(new MqttException(MqttException.REASON_CODE_BROKER_UNAVAILABLE)).when(mqttClient).publish(any(), any());

    assertThrows(MqttRuntimeException.class, () ->
      mqttDeviceCommandSender.sendCommandToDevice("channelId2", commandData));
  }

  @Test
  @SneakyThrows
  void shouldSendDeviceCommand() {
    CommandData commandData = new CommandData("commandId2", "commandContent");

    mqttDeviceCommandSender.sendCommandToDevice("channelId2", commandData);

    ArgumentCaptor<MqttMessage> messageCaptor = ArgumentCaptor.forClass(MqttMessage.class);
    verify(mqttClient).publish(eq("/devices/channelId2/commands"), messageCaptor.capture());
    String payload = new String(messageCaptor.getValue().getPayload());
    assertEquals("commandId2;commandContent", payload);
  }
}
