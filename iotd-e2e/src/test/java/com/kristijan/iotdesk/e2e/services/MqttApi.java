package com.kristijan.iotdesk.e2e.services;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;

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

  @SneakyThrows
  public Future<String> expectCommand(String topic) {
    final SettableListenableFuture<String> future = new SettableListenableFuture<>();
    mqttClient.subscribe(topic, (t, message) -> future.set(new String(message.getPayload())));
    return future;
  }

  @SneakyThrows
  public void publishCommandAcknowledgement(String channelId, String payload) {
    mqttClient.publish("devices/" + channelId + "/acks",
      new MqttMessage(payload.getBytes(StandardCharsets.UTF_8)));
    Thread.sleep(1000);
  }
}
