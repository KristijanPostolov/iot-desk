package com.kristijan.iotdesk.integration.tests.cases;

import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceChannelId;
import com.kristijan.iotdesk.domain.device.models.DeviceParameter;
import com.kristijan.iotdesk.domain.device.models.DeviceState;
import com.kristijan.iotdesk.domain.device.services.ChannelIdService;
import com.kristijan.iotdesk.domain.device.services.ListDevicesService;
import com.kristijan.iotdesk.domain.device.services.ManageDevicesService;
import com.kristijan.iotdesk.integration.tests.PostgresContainerTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class DeviceUseCasesIntegrationTest extends PostgresContainerTest {

  @Autowired
  private ListDevicesService listDevicesService;

  @Autowired
  private ManageDevicesService manageDevicesService;

  @Autowired
  private ChannelIdService channelIdService;

  @Autowired
  private Clock clock;

  @Test
  void shouldGetEmptyListWhenNoDevicesPresent() {
    assertEquals(0, listDevicesService.getAllDevices().size());
  }

  @Test
  void shouldReturnOneDeviceAfterCreating() {
    long deviceId = manageDevicesService.createNewDevice("Device A");

    List<Device> devices = listDevicesService.getAllDevices();

    assertEquals(1, devices.size());
    Device device = devices.get(0);
    assertEquals(deviceId, device.getId());
    assertEquals("Device A", device.getName());
    assertEquals(DeviceState.NEW, device.getState());
    assertEquals(LocalDateTime.now(clock), device.getCreatedAt());
    assertTrue(channelIdService.findByDeviceId(deviceId).isPresent());
  }

  @Test
  void shouldReturnUniqueIdsForEachDevice() {
    long deviceId1 = manageDevicesService.createNewDevice("Device A");
    long deviceId2 = manageDevicesService.createNewDevice("Device B");
    assertNotEquals(deviceId1, deviceId2);

    List<Device> devices = listDevicesService.getAllDevices();

    assertEquals(2, devices.size());
    Device device1 = devices.stream().filter(device -> device.getId() == deviceId1).findFirst().orElse(null);
    assertNewDevice(device1, "Device A");
    assertTrue(channelIdService.findByDeviceId(deviceId1).isPresent());

    Device device2 = devices.stream().filter(device -> device.getId() == deviceId2).findFirst().orElse(null);
    assertNewDevice(device2, "Device B");
    assertTrue(channelIdService.findByDeviceId(deviceId2).isPresent());
  }

  @Test
  void shouldReturnEmptyWhenDeviceWithGivenIdDoesNotExist() {
    long deviceId = manageDevicesService.createNewDevice("Device A");

    long nonExistingId = deviceId + 1;
    Optional<Device> result = listDevicesService.findById(nonExistingId);

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnDeviceById() {
    manageDevicesService.createNewDevice("Device A");
    long id = manageDevicesService.createNewDevice("Device B");
    manageDevicesService.createNewDevice("Device C");

    Optional<Device> result = listDevicesService.findById(id);

    assertTrue(result.isPresent());
    assertEquals(id, result.get().getId());
    assertEquals("Device B", result.get().getName());
  }

  @Test
  void shouldFindDeviceChannelIdForGivenDeviceIdOrReturnEmpty() {
    channelIdService.generateNewDeviceChannelId(1L);

    assertTrue(channelIdService.findByDeviceId(1L).isPresent());
    assertTrue(channelIdService.findByDeviceId(2L).isEmpty());
  }

  @Test
  void shouldFindDeviceChannelIdForGivenChannelIdOrReturnEmpty() {
    channelIdService.generateNewDeviceChannelId(1L);
    String existingChannelId = channelIdService.findByDeviceId(1L)
      .map(DeviceChannelId::getChannelId).orElse(null);

    assertTrue(channelIdService.findByChannelId(existingChannelId).isPresent());
    assertTrue(channelIdService.findByChannelId("nonExisting").isEmpty());
  }

  @Test
  void shouldActivateDevice() {
    long deviceId = manageDevicesService.createNewDevice("testDevice");

    manageDevicesService.activateDevice(deviceId);
    Device device = listDevicesService.findById(deviceId).orElse(null);

    assertNotNull(device);
    assertEquals("testDevice", device.getName());
    assertEquals(DeviceState.ACTIVE, device.getState());
  }

  @Test
  void shouldCreateParametersForDevice() {
    long deviceId = manageDevicesService.createNewDevice("testDevice");

    manageDevicesService.updateDeviceParameters(deviceId, Set.of(1, 2, 4));
    Optional<Device> deviceOptional = listDevicesService.findById(deviceId);

    assertTrue(deviceOptional.isPresent());
    Device device = deviceOptional.get();
    assertEquals("testDevice", device.getName());
    assertEquals(3, device.getParameters().size());
  }

  @Test
  void shouldCreateMissingParametersWhenSomeAlreadyExist() {
    long deviceId = manageDevicesService.createNewDevice("testDevice");

    Device device = manageDevicesService.updateDeviceParameters(deviceId, Set.of(1, 2));
    assertEquals(2, device.getParameters().size());

    device = manageDevicesService.updateDeviceParameters(deviceId, Set.of(2, 3));
    assertEquals(3, device.getParameters().size());
  }

  @Test
  void shouldSetUniqueIdsForDeviceParameters() {
    long deviceId = manageDevicesService.createNewDevice("testDevice");

    Device device = manageDevicesService.updateDeviceParameters(deviceId, Set.of(1, 2, 3));

    Set<Long> uniqueIds = device.getParameters().stream()
      .map(DeviceParameter::getId)
      .collect(Collectors.toSet());
    assertEquals(3, uniqueIds.size());
  }

  @Test
  void shouldPersistParametersSeparatelyForEachDevice() {
    long deviceId1 = manageDevicesService.createNewDevice("testDevice 1");
    manageDevicesService.updateDeviceParameters(deviceId1, Set.of(1, 2, 3));

    long deviceId2 = manageDevicesService.createNewDevice("testDevice 2");
    manageDevicesService.updateDeviceParameters(deviceId2, Set.of(3, 4));

    Device device1 = listDevicesService.findById(deviceId1).orElse(null);
    assertNotNull(device1);
    assertEquals(3, device1.getParameters().size());

    Device device2 = listDevicesService.findById(deviceId2).orElse(null);
    assertNotNull(device2);
    assertEquals(2, device2.getParameters().size());
  }

  private void assertNewDevice(Device device, String expectedName) {
    assertNotNull(device);
    assertEquals(expectedName, device.getName());
    assertEquals(DeviceState.NEW, device.getState());
  }
}
