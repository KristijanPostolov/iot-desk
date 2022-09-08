package com.kristijan.iotdesk.simulator;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@SpringBootApplication
@RequiredArgsConstructor
public class DeviceSimulatorApplication implements CommandLineRunner {

  private static final Random random = new Random();
  private static final String persistencePath = System.getProperty("user.dir") + "\\iotd-device-simulator\\_clients";

  private final SimulatorConfiguration simulatorConfiguration;

  public static void main(String[] args) {
    SpringApplication.run(DeviceSimulatorApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    System.out.println("Input channel id: ");
    String channelId = br.readLine().trim();
    MqttClient mqttClient = getMqttClient(channelId);
    String topic = "devices/" + channelId + "/snapshots";
    System.out.println("Enter number of parameters: ");
    int parameterNumber = Integer.parseInt(br.readLine());

    int messageNumber = 1;
    //noinspection InfiniteLoopStatement
    while (true) {
      List<String> measurements = new ArrayList<>();
      for (int i = 1; i <= parameterNumber; i++) {
        if (messageNumber % i == 0) {
          measurements.add(String.format(Locale.ROOT, "%d:%.2f", i, random.nextDouble()));
        }
      }
      String payload = String.join(",", measurements);
      System.out.println("Sending snapshot message to topic: " + topic + ", with payload: " + payload);
      mqttClient.publish(topic, new MqttMessage(payload.getBytes(StandardCharsets.UTF_8)));
      messageNumber++;
      //noinspection BusyWait
      Thread.sleep(5000);
    }
  }

  @SneakyThrows
  private MqttClient getMqttClient(String channelId) {
    MqttClient client = new MqttClient(simulatorConfiguration.getBrokerUrl(),
      simulatorConfiguration.getClientIdPrefix() + channelId, new MqttDefaultFilePersistence(persistencePath));
    client.connect();

    String commandTopic = "devices/" + channelId + "/commands";
    String acksTopic = "devices/" + channelId + "/acks";
    client.subscribe(commandTopic, (t, message) -> handleCommand(client, acksTopic, message));
    System.out.println("Subscribed to topic: " + commandTopic);
    return client;
  }

  @SneakyThrows
  private void handleCommand(MqttClient client, String acksTopic, MqttMessage message) {
    String payload = new String(message.getPayload());
    System.out.println("Received command: " + payload);
    Thread.sleep(10000);
    String commandId = payload.split(";")[0];
    String successful = random.nextBoolean() ? "1" : "0";
    String acknowledgement = commandId + ";" + successful;
    System.out.println("Sending acknowledgement message to topic: " + acksTopic + ", with payload: " + acknowledgement);
    client.publish(acksTopic, new MqttMessage(acknowledgement.getBytes(StandardCharsets.UTF_8)));
  }
}
