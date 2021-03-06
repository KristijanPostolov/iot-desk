package com.kristijan.iotdesk.integration.tests.cases;

import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceState;
import com.kristijan.iotdesk.domain.device.services.CreateDeviceService;
import com.kristijan.iotdesk.domain.device.services.ListDevicesService;
import com.kristijan.iotdesk.integration.tests.IntegrationTestConfiguration;
import com.kristijan.iotdesk.persistence.mock.repositories.DevicesRepositoryMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = IntegrationTestConfiguration.class)
public class DeviceUseCasesIntegrationTest {

  @Autowired
  private ListDevicesService listDevicesService;

  @Autowired
  private CreateDeviceService createDeviceService;

  @Autowired
  private DevicesRepositoryMock devicesRepository;

  @AfterEach
  void tearDown() {
    devicesRepository.reset();
  }

  @Test
  void shouldGetEmptyListWhenNoDevicesPresent() {
    assertEquals(0, listDevicesService.getAllDevices().size());
  }

  @Test
  void shouldReturnOneDeviceAfterCreating() {
    long deviceId = createDeviceService.createNewDevice("Device A");

    List<Device> devices = listDevicesService.getAllDevices();

    assertEquals(1, devices.size());
    Device device = devices.get(0);
    assertEquals(deviceId, device.getId());
    assertEquals("Device A", device.getName());
    assertEquals(DeviceState.NEW, device.getState());
  }

  @Test
  void shouldReturnUniqueIdsForEachDevice() {
    long deviceId1 = createDeviceService.createNewDevice("Device A");
    long deviceId2 = createDeviceService.createNewDevice("Device B");
    assertNotEquals(deviceId1, deviceId2);

    List<Device> devices = listDevicesService.getAllDevices();

    assertEquals(2, devices.size());
    Device device1 = devices.stream().filter(device -> device.getId() == deviceId1).findFirst().orElse(null);
    assertNewDevice(device1, "Device A");

    Device device2 = devices.stream().filter(device -> device.getId() == deviceId2).findFirst().orElse(null);
    assertNewDevice(device2, "Device B");
  }

  private void assertNewDevice(Device device, String expectedName) {
    assertNotNull(device);
    assertEquals(expectedName, device.getName());
    assertEquals(DeviceState.NEW, device.getState());
  }
}
