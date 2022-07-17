package com.kristijan.iotdesk.application.services;

import com.kristijan.iotdesk.application.dtos.DeviceDto;
import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.services.ListDevicesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DevicesApplicationServiceTest {

  @InjectMocks
  private DevicesApplicationService devicesApplicationService;

  @Mock
  private ListDevicesService listDevicesServiceMock;

  @Test
  void shouldReturnEmpty() {
    assertEquals(0, devicesApplicationService.getAllDevices().size());
  }

  @Test
  void shouldReturnMultipleDevices() {
    List<Device> existingDevices =
      List.of(new Device("d1"), new Device("d2"), new Device("d3"));
    when(listDevicesServiceMock.getAllDevices()).thenReturn(existingDevices);
    List<DeviceDto> result = devicesApplicationService.getAllDevices();

    assertEquals(3, result.size());
  }

  @Test
  void shouldMapDevicesToDeviceDtos() {
    List<Device> existingDevices =
      List.of(new Device("d1"), new Device("d2"), new Device("d3"));
    when(listDevicesServiceMock.getAllDevices()).thenReturn(existingDevices);
    List<DeviceDto> result = devicesApplicationService.getAllDevices();

    assertEquals(3, result.size());
    assertEquals("d1", result.get(0).getName());
    assertEquals("d2", result.get(1).getName());
    assertEquals("d3", result.get(2).getName());
  }

}
