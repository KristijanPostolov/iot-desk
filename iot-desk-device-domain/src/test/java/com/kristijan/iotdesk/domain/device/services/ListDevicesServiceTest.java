package com.kristijan.iotdesk.domain.device.services;

import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceState;
import com.kristijan.iotdesk.domain.device.repositories.DevicesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ListDevicesServiceTest {

  @InjectMocks
  private ListDevicesService listDevicesService;

  @Mock
  private DevicesRepository devicesRepositoryMock;

  @Test
  void shouldReturnEmptyWhenRepositoryReturnsNullList() {
    when(devicesRepositoryMock.findAll())
      .thenReturn(null);
    List<Device> devices = listDevicesService.getAllDevices();

    assertTrue(devices.isEmpty());
  }

  @Test
  void shouldReturnEmptyDeviceList() {
    when(devicesRepositoryMock.findAll())
      .thenReturn(Collections.emptyList());
    List<Device> devices = listDevicesService.getAllDevices();

    assertTrue(devices.isEmpty());
  }

  @Test
  void shouldReturnMultipleDevices() {
    List<Device> existingDevices =
      List.of(createDevice(1L, "d1"),
        createDevice(2L, "d2"),
        createDevice(3L, "d3"));
    when(devicesRepositoryMock.findAll())
      .thenReturn(existingDevices);
    List<Device> devices = listDevicesService.getAllDevices();

    assertEquals(3, devices.size());
    assertDeviceDto(devices.get(0), 1L, "d1");
    assertDeviceDto(devices.get(1), 2L, "d2");
    assertDeviceDto(devices.get(2), 3L, "d3");
  }

  void assertDeviceDto(Device device, Long id, String name) {
    assertEquals(id, device.getId());
    assertEquals(name, device.getName());
    assertEquals(DeviceState.NEW, device.getState());
  }

  private Device createDevice(Long id, String name) {
    Device device = new Device(name, DeviceState.NEW);
    device.setId(id);
    return device;
  }


}

