package com.kristijan.iotdesk.messaging.mqtt.services;

import com.kristijan.iotdesk.domain.snapshots.models.AnchorSnapshot;
import com.kristijan.iotdesk.domain.snapshots.models.DeviceSnapshot;
import com.kristijan.iotdesk.domain.snapshots.services.AddDeviceSnapshotService;
import com.kristijan.iotdesk.domain.snapshots.services.DeviceMessagingErrorHandler;
import com.kristijan.iotdesk.messaging.mqtt.models.ParsingResult;
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
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class DeviceSnapshotHandlerTest {

  private DeviceSnapshotHandler deviceSnapshotHandler;

  @Mock
  private DeviceMessagingErrorHandler deviceMessagingErrorHandler;

  @Mock
  private MqttPayloadValidatorAndParser mqttPayloadValidatorAndParser;

  @Mock
  private AddDeviceSnapshotService addDeviceSnapshotService;

  private final LocalDateTime now = LocalDateTime.parse("2022-08-13T19:10:00");
  private final Clock clock = Clock.fixed(now.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);

  @BeforeEach
  void setUp() {
    deviceSnapshotHandler = new DeviceSnapshotHandler(deviceMessagingErrorHandler, mqttPayloadValidatorAndParser,
      addDeviceSnapshotService, clock);
  }

  @Test
  void shouldReturnFalseForPayloadValidationError() {
    when(mqttPayloadValidatorAndParser.parsePayload(any())).thenReturn(ParsingResult.invalid());

    boolean result = deviceSnapshotHandler.handleSnapshot("channelId1", new byte[0]);

    assertFalse(result);
    verify(deviceMessagingErrorHandler).invalidPayload("channelId1");
  }

  @Test
  void shouldReturnFalseIfAddingIsNotSuccessful() {
    when(mqttPayloadValidatorAndParser.parsePayload(any())).thenReturn(
      ParsingResult.of(Collections.emptyList()));

    boolean result = deviceSnapshotHandler.handleSnapshot("channelId1", new byte[0]);

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

    boolean result = deviceSnapshotHandler.handleSnapshot("channelId1", new byte[0]);

    assertTrue(result);
    ArgumentCaptor<DeviceSnapshot> snapshotCaptor = ArgumentCaptor.forClass(DeviceSnapshot.class);
    verify(addDeviceSnapshotService).addDeviceSnapshot(snapshotCaptor.capture());
    DeviceSnapshot deviceSnapshot = snapshotCaptor.getValue();
    assertEquals("channelId1", deviceSnapshot.getChannelId());
    assertEquals(now, deviceSnapshot.getTimestamp());
    assertEquals(anchorSnapshots, deviceSnapshot.getAnchorSnapshots());
  }

}