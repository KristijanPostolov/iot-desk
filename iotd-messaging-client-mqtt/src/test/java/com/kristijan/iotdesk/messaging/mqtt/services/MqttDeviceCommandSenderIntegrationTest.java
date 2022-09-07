package com.kristijan.iotdesk.messaging.mqtt.services;

import com.kristijan.iotdesk.domain.snapshots.models.CommandData;
import com.kristijan.iotdesk.messaging.mqtt.MqttContainerTest;
import com.kristijan.iotdesk.messaging.mqtt.MqttIntegrationTestConfig;
import lombok.SneakyThrows;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest(classes = {MqttIntegrationTestConfig.class})
class MqttDeviceCommandSenderIntegrationTest extends MqttContainerTest {

  @Autowired
  private MqttClient mqttClient;

  @Autowired
  private MqttDeviceCommandSender mqttDeviceCommandSender;

  @Test
  @SneakyThrows
  void shouldSendDeviceCommandMessage() {
    CommandData commandData = new CommandData("commandId123", "commandMessage");
    final Semaphore callbackSemaphore = new Semaphore(0);
    mqttClient.subscribe("/devices/channelId1/commands", ((topic, message) -> {
      String payload = new String(message.getPayload());
      assertEquals("commandId123;commandMessage", payload);
      callbackSemaphore.release();
    }));

    mqttDeviceCommandSender.sendCommandToDevice("channelId1", commandData);

    // Waiting for the async callback to be executed, otherwise fail the test.
    boolean callbackHandled = callbackSemaphore.tryAcquire(3, TimeUnit.SECONDS);
    assertTrue(callbackHandled);
  }
}