package com.kristijan.iotdesk.integration.tests.cases;

import com.kristijan.iotdesk.domain.device.models.DeviceChannelId;
import com.kristijan.iotdesk.domain.device.services.ChannelIdService;
import com.kristijan.iotdesk.domain.device.services.ManageDevicesService;
import com.kristijan.iotdesk.domain.snapshots.models.AcknowledgementStatus;
import com.kristijan.iotdesk.domain.snapshots.models.CommandAck;
import com.kristijan.iotdesk.domain.snapshots.models.DeviceCommand;
import com.kristijan.iotdesk.domain.snapshots.services.DeviceCommandAckService;
import com.kristijan.iotdesk.domain.snapshots.services.DeviceCommandService;
import com.kristijan.iotdesk.integration.tests.PostgresContainerTest;
import com.kristijan.iotdesk.jpa.repositories.DeviceCommandRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DeviceCommandUseCaseTest extends PostgresContainerTest {

  @Autowired
  private ManageDevicesService manageDevicesService;

  @Autowired
  private ChannelIdService channelIdService;

  @Autowired
  private DeviceCommandService deviceCommandService;

  @Autowired
  private DeviceCommandAckService deviceCommandAckService;

  @Autowired
  private DeviceCommandRepositoryImpl repository;

  @Autowired
  private Clock clock;

  private final LocalDateTime now = LocalDateTime.parse("2022-09-07T22:30:00");

  @Test
  void shouldNotAllowMultipleCommandsWithSameCommandId() {
    long deviceId = manageDevicesService.createNewDevice("Device 1");

    DeviceCommand deviceCommand1 = new DeviceCommand("commandId1", "commandContent1", deviceId,
      now.plusMinutes(1), AcknowledgementStatus.NO_ACK);
    DeviceCommand deviceCommand2 = new DeviceCommand("commandId1", "commandContent2", deviceId,
      now.plusMinutes(5), AcknowledgementStatus.NO_ACK);

    repository.save(deviceCommand1);
    assertThrows(Exception.class, () -> repository.save(deviceCommand2));
  }

  @Test
  void shouldSaveAndFetchDeviceCommands() {
    long deviceId = manageDevicesService.createNewDevice("Device 1");
    DeviceCommand deviceCommand1 = new DeviceCommand(null, "commandId1", "commandContent1", deviceId,
      now.plusMinutes(1), now.plusMinutes(5), AcknowledgementStatus.ACK_SUCCESSFUL);
    DeviceCommand deviceCommand2 = new DeviceCommand(null, "commandId2", "commandContent2", deviceId,
      now.plusMinutes(7), now.plusMinutes(15), AcknowledgementStatus.ACK_SUCCESSFUL);
    DeviceCommand deviceCommand3 = new DeviceCommand("commandId3", "commandContent3", deviceId,
      now.plusMinutes(4), AcknowledgementStatus.NO_ACK);
    DeviceCommand deviceCommand4 = new DeviceCommand("commandId4", "commandContent4", deviceId,
      now.plusMinutes(30), AcknowledgementStatus.NO_ACK);

    repository.save(deviceCommand1);
    repository.save(deviceCommand2);
    repository.save(deviceCommand3);
    repository.save(deviceCommand4);

    List<DeviceCommand> result = deviceCommandService.getCommandsInTimeRange(deviceId, now.plusMinutes(2),
      now.plusMinutes(10));
    assertEquals(2, result.size());
    assertDeviceCommand(deviceCommand3, result.get(0));
    assertDeviceCommand(deviceCommand2, result.get(1));
  }

  @Test
  void shouldFindCommandByCommandId() {
    long deviceId = manageDevicesService.createNewDevice("Device 1");
    DeviceCommand deviceCommand = new DeviceCommand("commandId1", "commandContent1", deviceId,
      now.plusMinutes(1), AcknowledgementStatus.NO_ACK);
    repository.save(deviceCommand);

    Optional<DeviceCommand> saved = repository.findByCommandId(deviceCommand.getCommandId());

    assertTrue(saved.isPresent());
    assertDeviceCommand(deviceCommand, saved.get());
  }

  @Test
  void shouldUpdateAcknowledgementTimestampAndStatus() {
    long deviceId = manageDevicesService.createNewDevice("Device 1");
    String channelId = channelIdService.findByDeviceId(deviceId).map(DeviceChannelId::getChannelId).orElse(null);
    DeviceCommand deviceCommand = new DeviceCommand("commandId1", "commandContent1", deviceId,
      now.plusMinutes(1), AcknowledgementStatus.NO_ACK);
    repository.save(deviceCommand);

    deviceCommandAckService.acknowledgeCommand(channelId, new CommandAck(deviceCommand.getCommandId(), true));

    Optional<DeviceCommand> saved = repository.findByCommandId(deviceCommand.getCommandId());
    assertTrue(saved.isPresent());
    DeviceCommand expected = new DeviceCommand(null, "commandId1", "commandContent1", deviceId,
      now.plusMinutes(1), LocalDateTime.now(clock), AcknowledgementStatus.ACK_SUCCESSFUL);
    assertDeviceCommand(expected, saved.get());
  }

  private void assertDeviceCommand(DeviceCommand expected, DeviceCommand actual) {
    assertNotNull(expected);
    assertEquals(expected.getCommandId(), actual.getCommandId());
    assertEquals(expected.getContent(), actual.getContent());
    assertEquals(expected.getDeviceId(), actual.getDeviceId());
    assertEquals(expected.getSentAt(), actual.getSentAt());
    assertEquals(expected.getAcknowledgedAt(), actual.getAcknowledgedAt());
    assertEquals(expected.getAckStatus(), actual.getAckStatus());
  }


}
