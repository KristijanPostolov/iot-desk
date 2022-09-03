package com.kristijan.iotdesk.e2e.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MqttApi {

  private final MqttClient mqttClient;

  @SneakyThrows
  public void publishDeviceSnapshot(String channelId, String payload) {
    mqttClient.publish("devices/" + channelId + "/snapshots",
      new MqttMessage(payload.getBytes(StandardCharsets.UTF_8)));
    Thread.sleep(1000);
  }

}
