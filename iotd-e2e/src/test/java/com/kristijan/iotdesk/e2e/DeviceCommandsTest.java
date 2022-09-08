package com.kristijan.iotdesk.e2e;

import com.kristijan.iotdesk.application.dtos.DeviceCommandDto;
import com.kristijan.iotdesk.e2e.config.E2EConfiguration;
import com.kristijan.iotdesk.e2e.services.IotDeskServerApi;
import com.kristijan.iotdesk.e2e.services.MqttApi;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = E2EConfiguration.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class DeviceCommandsTest {

  @Autowired
  private IotDeskServerApi serverApi;

  @Autowired
  private MqttApi mqttApi;

  @Test
  @SneakyThrows
  void shouldPostAndSaveCommand() {
    Long deviceId = serverApi.createNewDevice();
    String channelId = serverApi.getChannelId(deviceId);
    mqttApi.publishDeviceSnapshot(channelId, "1:1.3,2:5.5");
    Thread.sleep(1000);

    Future<String> commandFuture1 = mqttApi.expectCommand("devices/" + channelId + "/commands");
    serverApi.sendCommand(deviceId, "command1");
    String command1 = commandFuture1.get(1, TimeUnit.SECONDS);
    assertTrue(command1.endsWith(";command1"));

    Future<String> commandFuture2 = mqttApi.expectCommand("devices/" + channelId + "/commands");
    serverApi.sendCommand(deviceId, "command2");
    String command2 = commandFuture2.get(1, TimeUnit.SECONDS);
    assertTrue(command2.endsWith(";command2"));

    Instant now = Instant.now();
    List<DeviceCommandDto> commands = serverApi.getCommands(deviceId, now.minusSeconds(3600), now.plusSeconds(3600));
    assertEquals(2, commands.size());
    assertEquals("command1", commands.get(0).getContent());
    assertEquals("command2", commands.get(1).getContent());
  }
}
