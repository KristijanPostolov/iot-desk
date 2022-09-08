package com.kristijan.iotdesk.messaging.mqtt.services;

import com.kristijan.iotdesk.domain.device.exceptions.DomainException;
import com.kristijan.iotdesk.domain.snapshots.models.AnchorSnapshot;
import com.kristijan.iotdesk.domain.snapshots.models.DeviceSnapshot;
import com.kristijan.iotdesk.domain.snapshots.services.AddDeviceSnapshotService;
import com.kristijan.iotdesk.domain.snapshots.services.DeviceMessagingErrorHandler;
import com.kristijan.iotdesk.messaging.mqtt.models.ParsingResult;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class DeviceSnapshotHandlerTest {

  private final static String TOPIC_NAME = "devices/channelId1/snapshots";

  private DeviceSnapshotHandler deviceSnapshotHandler;

  @Mock
  private DeviceMessagingErrorHandler deviceMessagingErrorHandler;

  @Mock
  private MqttSnapshotValidatorAndParser mqttSnapshotValidatorAndParser;

  @Mock
  private AddDeviceSnapshotService addDeviceSnapshotService;

  private final LocalDateTime now = LocalDateTime.parse("2022-08-13T19:10:00");
  private final Clock clock = Clock.fixed(now.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);

  @BeforeEach
  void setUp() {
    deviceSnapshotHandler = new DeviceSnapshotHandler(deviceMessagingErrorHandler, mqttSnapshotValidatorAndParser,
      addDeviceSnapshotService, clock);
  }

  @Test
  void shouldMapTopicToChannelId() {
    when(mqttSnapshotValidatorAndParser.parsePayload(any())).thenReturn(
      ParsingResult.of(Collections.emptyList()));
    when(addDeviceSnapshotService.addDeviceSnapshot(any())).thenReturn(true);

    deviceSnapshotHandler.handleMqttMessage(TOPIC_NAME, new MqttMessage(new byte[0]));
    ArgumentCaptor<DeviceSnapshot> snapshotCaptor = ArgumentCaptor.forClass(DeviceSnapshot.class);
    verify(addDeviceSnapshotService).addDeviceSnapshot(snapshotCaptor.capture());
    assertEquals("channelId1", snapshotCaptor.getValue().getChannelId());
  }

  @Test
  void shouldHandlePayloadValidationError() {
    when(mqttSnapshotValidatorAndParser.parsePayload(any())).thenReturn(ParsingResult.invalid());

    deviceSnapshotHandler.handleMqttMessage(TOPIC_NAME, new MqttMessage(new byte[0]));

    verify(deviceMessagingErrorHandler).invalidSnapshotPayload("channelId1");
  }

  @Test
  void shouldHandleDomainError() {
    when(mqttSnapshotValidatorAndParser.parsePayload(any())).thenReturn(
      ParsingResult.of(Collections.emptyList()));
    DomainException exception = new DomainException("error");
    when(addDeviceSnapshotService.addDeviceSnapshot(any())).thenThrow(exception);

    deviceSnapshotHandler.handleMqttMessage(TOPIC_NAME, new MqttMessage(new byte[0]));

    verify(deviceMessagingErrorHandler).handleSnapshotDomainException("channelId1", exception);
  }

  @Test
  void shouldNotFailIfAddingSnapshotIsNotSuccessful() {
    when(mqttSnapshotValidatorAndParser.parsePayload(any())).thenReturn(
      ParsingResult.of(Collections.emptyList()));

    deviceSnapshotHandler.handleMqttMessage(TOPIC_NAME, new MqttMessage(new byte[0]));
  }

  @Test
  void shouldAddSnapshotForValidPayload() {
    List<AnchorSnapshot> anchorSnapshots = List.of(
      new AnchorSnapshot(1, 1.2),
      new AnchorSnapshot(2, 123456),
      new AnchorSnapshot(3, 123.456789));
    when(mqttSnapshotValidatorAndParser.parsePayload(any())).thenReturn(
      ParsingResult.of(anchorSnapshots));
    when(addDeviceSnapshotService.addDeviceSnapshot(any())).thenReturn(true);

    deviceSnapshotHandler.handleMqttMessage(TOPIC_NAME, new MqttMessage(new byte[0]));

    ArgumentCaptor<DeviceSnapshot> snapshotCaptor = ArgumentCaptor.forClass(DeviceSnapshot.class);
    verify(addDeviceSnapshotService).addDeviceSnapshot(snapshotCaptor.capture());
    DeviceSnapshot deviceSnapshot = snapshotCaptor.getValue();
    assertEquals("channelId1", deviceSnapshot.getChannelId());
    assertEquals(now, deviceSnapshot.getTimestamp());
    assertEquals(anchorSnapshots, deviceSnapshot.getAnchorSnapshots());
  }

}