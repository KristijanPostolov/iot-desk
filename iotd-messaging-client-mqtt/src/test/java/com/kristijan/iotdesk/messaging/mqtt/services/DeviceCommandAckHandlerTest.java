package com.kristijan.iotdesk.messaging.mqtt.services;

import com.kristijan.iotdesk.domain.snapshots.models.CommandAck;
import com.kristijan.iotdesk.domain.snapshots.services.DeviceCommandAckService;
import com.kristijan.iotdesk.domain.snapshots.services.DeviceMessagingErrorHandler;
import com.kristijan.iotdesk.messaging.mqtt.models.ParsingResult;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeviceCommandAckHandlerTest {

  @InjectMocks
  private DeviceCommandAckHandler deviceCommandAckHandler;

  @Mock
  private MqttCommandAckValidatorAndParser mqttCommandAckValidatorAndParser;
  @Mock
  private DeviceMessagingErrorHandler deviceMessagingErrorHandler;
  @Mock
  private DeviceCommandAckService deviceCommandAckService;

  @Test
  void shouldHandleErrorForInvalidAckPayload() {
    when(mqttCommandAckValidatorAndParser.parsePayload(any())).thenReturn(ParsingResult.invalid());

    deviceCommandAckHandler.handleMqttMessage("devices/channelId123/acks", new MqttMessage(new byte[0]));

    verify(deviceMessagingErrorHandler).invalidCommandAckPayload("channelId123");
  }

  @Test
  void shouldAcknowledgeCommandOnValidPayload() {
    CommandAck commandAck = new CommandAck("commandId1", true);
    when(mqttCommandAckValidatorAndParser.parsePayload(any())).thenReturn(ParsingResult.of(commandAck));

    deviceCommandAckHandler.handleMqttMessage("devices/channelId123/acks", new MqttMessage(new byte[0]));

    verify(deviceCommandAckService).acknowledgeCommand("channelId123", commandAck);
  }

  @Test
  void shouldHandleDomainExceptionOnCommandAcknowledgement() {
    CommandAck commandAck = new CommandAck("commandId1", true);
    when(mqttCommandAckValidatorAndParser.parsePayload(any())).thenReturn(ParsingResult.of(commandAck));
    RuntimeException exception = new RuntimeException();
    when(deviceCommandAckService.acknowledgeCommand("channelId1", commandAck))
      .thenThrow(exception);

    deviceCommandAckHandler.handleMqttMessage("devices/channelId1/acks", new MqttMessage(new byte[0]));

    verify(deviceMessagingErrorHandler).handleCommandAckDomainException("channelId1", exception);
  }
}
