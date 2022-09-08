package com.kristijan.iotdesk.messaging.mqtt.services;

import com.kristijan.iotdesk.domain.snapshots.models.AnchorSnapshot;
import com.kristijan.iotdesk.domain.snapshots.models.DeviceSnapshot;
import com.kristijan.iotdesk.domain.snapshots.services.AddDeviceSnapshotService;
import com.kristijan.iotdesk.messaging.mqtt.MqttContainerTest;
import com.kristijan.iotdesk.messaging.mqtt.MqttIntegrationTestConfig;
import lombok.SneakyThrows;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {MqttIntegrationTestConfig.class})
@Disabled
public class DeviceSnapshotHandlerIntegrationTest extends MqttContainerTest {

  @MockBean
  private AddDeviceSnapshotService addDeviceSnapshotService;

  @Autowired
  private MqttClient mqttClient;

  private final String channelId = UUID.randomUUID().toString();

  @Test
  @SneakyThrows
  void shouldHandleDeviceSnapshotMqttMessage() {
    when(addDeviceSnapshotService.addDeviceSnapshot(any())).thenReturn(true);

    mqttClient.publish("devices/" + channelId + "/snapshots",
      new MqttMessage("1:1.5,2:55".getBytes(StandardCharsets.UTF_8)));

    ArgumentCaptor<DeviceSnapshot> captor = ArgumentCaptor.forClass(DeviceSnapshot.class);
    verify(addDeviceSnapshotService).addDeviceSnapshot(captor.capture());
    DeviceSnapshot deviceSnapshot = captor.getValue();
    assertEquals(channelId, deviceSnapshot.getChannelId());
    assertEquals(MqttIntegrationTestConfig.NOW, deviceSnapshot.getTimestamp());
    assertAnchorSnapshot(deviceSnapshot.getAnchorSnapshots().get(0), 1, 1.5);
    assertAnchorSnapshot(deviceSnapshot.getAnchorSnapshots().get(1), 2, 55);
  }

  @Test
  @SneakyThrows
  void shouldNotHandleMessagesFromDifferentTopics() {
    mqttClient.publish("devices",
      new MqttMessage("1:1.5,2:55".getBytes(StandardCharsets.UTF_8)));

    verify(addDeviceSnapshotService, times(0)).addDeviceSnapshot(any());
  }

  private void assertAnchorSnapshot(AnchorSnapshot snapshot, int expectedAnchor, double expectedValue) {
    assertNotNull(snapshot);
    assertEquals(expectedAnchor, snapshot.getAnchor());
    assertEquals(expectedValue, snapshot.getValue());
  }
}
