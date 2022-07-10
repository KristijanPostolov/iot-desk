package com.kristijan.iotdesk.domain.device.services;

import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.repositories.ListDevicesRepository;
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
  private ListDevicesRepository listDevicesRepositoryMock;

  @Test
  void shouldReturnEmptyWhenRepositoryReturnsNullList() {
    when(listDevicesRepositoryMock.findAll())
      .thenReturn(null);
    List<Device> devices = listDevicesService.getAllDevices();

    assertTrue(devices.isEmpty());
  }

  @Test
  void shouldReturnEmptyDeviceList() {
    when(listDevicesRepositoryMock.findAll())
      .thenReturn(Collections.emptyList());
    List<Device> devices = listDevicesService.getAllDevices();

    assertTrue(devices.isEmpty());
  }

  @Test
  void shouldReturnMultipleDevices() {
    List<Device> existingDevices =
      List.of(new Device("d1"), new Device("d2"), new Device("d3"));
    when(listDevicesRepositoryMock.findAll())
      .thenReturn(existingDevices);
    List<Device> devices = listDevicesService.getAllDevices();

    assertEquals(3, devices.size());
    assertEquals("d1", devices.get(0).getName());
    assertEquals("d2", devices.get(1).getName());
    assertEquals("d3", devices.get(2).getName());
  }

}

