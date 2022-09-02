package com.kristijan.iotdesk.messaging.mqtt.services;

import com.kristijan.iotdesk.domain.device.exceptions.DomainException;
import com.kristijan.iotdesk.domain.snapshots.models.AnchorSnapshot;
import com.kristijan.iotdesk.domain.snapshots.models.DeviceSnapshot;
import com.kristijan.iotdesk.domain.snapshots.services.AddDeviceSnapshotService;
import com.kristijan.iotdesk.domain.snapshots.services.DeviceMessagingErrorHandler;
import com.kristijan.iotdesk.messaging.mqtt.models.ParsingResult;
import lombok.SneakyThrows;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class DeviceSnapshotHandlerTest {

  private final static String TOPIC_NAME = "devices/channelId1/snapshots";

  private DeviceSnapshotHandler deviceSnapshotHandler;

  @Mock
  private DeviceMessagingErrorHandler deviceMessagingErrorHandler;

  @Mock
  private MqttPayloadValidatorAndParser mqttPayloadValidatorAndParser;

  @Mock
  private AddDeviceSnapshotService addDeviceSnapshotService;

  @Mock
  private MqttClient mqttClient;

  private final LocalDateTime now = LocalDateTime.parse("2022-08-13T19:10:00");
  private final Clock clock = Clock.fixed(now.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);

  @BeforeEach
  void setUp() {
    deviceSnapshotHandler = new DeviceSnapshotHandler(deviceMessagingErrorHandler, mqttPayloadValidatorAndParser,
      addDeviceSnapshotService, clock, mqttClient);
  }

  @Test
  void shouldMapTopicToChannelId() {
    when(mqttPayloadValidatorAndParser.parsePayload(any())).thenReturn(
      ParsingResult.of(Collections.emptyList()));
    when(addDeviceSnapshotService.addDeviceSnapshot(any())).thenReturn(true);

    deviceSnapshotHandler.handleSnapshotMessage(TOPIC_NAME, new MqttMessage(new byte[0]));
    ArgumentCaptor<DeviceSnapshot> snapshotCaptor = ArgumentCaptor.forClass(DeviceSnapshot.class);
    verify(addDeviceSnapshotService).addDeviceSnapshot(snapshotCaptor.capture());
    assertEquals("channelId1", snapshotCaptor.getValue().getChannelId());
  }

  @Test
  void shouldReturnFalseForPayloadValidationError() {
    when(mqttPayloadValidatorAndParser.parsePayload(any())).thenReturn(ParsingResult.invalid());

    boolean result = deviceSnapshotHandler.handleSnapshotMessage(TOPIC_NAME, new MqttMessage(new byte[0]));

    assertFalse(result);
    verify(deviceMessagingErrorHandler).invalidPayload("channelId1");
  }

  @Test
  void shouldReturnFalseOnDomainError() {
    when(mqttPayloadValidatorAndParser.parsePayload(any())).thenReturn(
      ParsingResult.of(Collections.emptyList()));
    DomainException exception = new DomainException("error");
    when(addDeviceSnapshotService.addDeviceSnapshot(any())).thenThrow(exception);

    boolean result = deviceSnapshotHandler.handleSnapshotMessage(TOPIC_NAME, new MqttMessage(new byte[0]));

    assertFalse(result);
    verify(deviceMessagingErrorHandler).handleDomainException("channelId1", exception);
  }

  @Test
  void shouldReturnFalseIfAddingIsNotSuccessful() {
    when(mqttPayloadValidatorAndParser.parsePayload(any())).thenReturn(
      ParsingResult.of(Collections.emptyList()));

    boolean result = deviceSnapshotHandler.handleSnapshotMessage(TOPIC_NAME, new MqttMessage(new byte[0]));

    assertFalse(result);
  }

  @Test
  void shouldAddSnapshotForValidPayload() {
    List<AnchorSnapshot> anchorSnapshots = List.of(
      new AnchorSnapshot(1, 1.2),
      new AnchorSnapshot(2, 123456),
      new AnchorSnapshot(3, 123.456789));
    when(mqttPayloadValidatorAndParser.parsePayload(any())).thenReturn(
      ParsingResult.of(anchorSnapshots));
    when(addDeviceSnapshotService.addDeviceSnapshot(any())).thenReturn(true);

    boolean result = deviceSnapshotHandler.handleSnapshotMessage(TOPIC_NAME, new MqttMessage(new byte[0]));

    assertTrue(result);
    ArgumentCaptor<DeviceSnapshot> snapshotCaptor = ArgumentCaptor.forClass(DeviceSnapshot.class);
    verify(addDeviceSnapshotService).addDeviceSnapshot(snapshotCaptor.capture());
    DeviceSnapshot deviceSnapshot = snapshotCaptor.getValue();
    assertEquals("channelId1", deviceSnapshot.getChannelId());
    assertEquals(now, deviceSnapshot.getTimestamp());
    assertEquals(anchorSnapshots, deviceSnapshot.getAnchorSnapshots());
  }

  @Test
  @SneakyThrows
  void shouldThrowIfTopicSubscriptionFails() {
    doThrow(new MqttException(MqttException.REASON_CODE_BROKER_UNAVAILABLE))
      .when(mqttClient).subscribe(anyString(), any());

    assertThrows(RuntimeException.class, () -> new DeviceSnapshotHandler(deviceMessagingErrorHandler,
      mqttPayloadValidatorAndParser, addDeviceSnapshotService, clock, mqttClient));
  }
}