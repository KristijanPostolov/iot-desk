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
      List.of(new Device("d1", DeviceState.NEW),
        new Device("d2", DeviceState.NEW),
        new Device("d3", DeviceState.NEW));
    when(devicesRepositoryMock.findAll())
      .thenReturn(existingDevices);
    List<Device> devices = listDevicesService.getAllDevices();

    assertEquals(3, devices.size());
    assertEquals("d1", devices.get(0).getName());
    assertEquals(DeviceState.NEW, devices.get(0).getState());
    assertEquals("d2", devices.get(1).getName());
    assertEquals(DeviceState.NEW, devices.get(1).getState());
    assertEquals("d3", devices.get(2).getName());
    assertEquals(DeviceState.NEW, devices.get(2).getState());
  }

}

