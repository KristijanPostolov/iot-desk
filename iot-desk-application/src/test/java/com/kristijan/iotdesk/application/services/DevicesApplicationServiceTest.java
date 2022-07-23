package com.kristijan.iotdesk.application.services;

import com.kristijan.iotdesk.application.dtos.CreateDeviceDto;
import com.kristijan.iotdesk.application.dtos.DeviceDto;
import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceState;
import com.kristijan.iotdesk.domain.device.services.CreateDeviceService;
import com.kristijan.iotdesk.domain.device.services.ListDevicesService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DevicesApplicationServiceTest {

  @InjectMocks
  private DevicesApplicationService devicesApplicationService;

  @Mock
  private ListDevicesService listDevicesServiceMock;

  @Mock
  private CreateDeviceService createDeviceServiceMock;

  @Test
  void shouldReturnEmpty() {
    assertEquals(0, devicesApplicationService.getAllDevices().size());
  }

  @Test
  void shouldMapDevicesToDeviceDtos() {
    List<Device> existingDevices =
      List.of(createDevice(1L, "d1"),
        createDevice(2L, "d2"),
        createDevice(3L, "d3"));
    when(listDevicesServiceMock.getAllDevices()).thenReturn(existingDevices);
    List<DeviceDto> result = devicesApplicationService.getAllDevices();

    assertEquals(3, result.size());
    assertDeviceDto(result.get(0), 1, "d1", DeviceState.NEW);
    assertDeviceDto(result.get(1), 2, "d2", DeviceState.NEW);
    assertDeviceDto(result.get(2), 3, "d3", DeviceState.NEW);
  }

  @Test
  void shouldReturnIdForEachDevice() {
    CreateDeviceDto dto1 = new CreateDeviceDto("New Device 1");
    when(createDeviceServiceMock.createNewDevice(eq(dto1.getName()))).thenReturn(5L);
    CreateDeviceDto dto2 = new CreateDeviceDto("New Device 2");
    when(createDeviceServiceMock.createNewDevice(eq(dto2.getName()))).thenReturn(6L);

    long result1 = devicesApplicationService.createNewDevice(dto1);
    long result2 = devicesApplicationService.createNewDevice(dto2);

    assertEquals(5L, result1);
    assertEquals(6L, result2);
  }

  void assertDeviceDto(DeviceDto dto, long id, String name, DeviceState state) {
    assertEquals(id, dto.getId());
    assertEquals(name, dto.getName());
    assertEquals(state, dto.getState());
  }

  private Device createDevice(Long id, String d1) {
    Device device = new Device(d1, DeviceState.NEW);
    device.setId(id);
    return device;
  }
}
