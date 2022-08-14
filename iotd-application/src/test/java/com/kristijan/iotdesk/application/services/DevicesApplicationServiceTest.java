package com.kristijan.iotdesk.application.services;

import com.kristijan.iotdesk.application.dtos.ChannelIdDto;
import com.kristijan.iotdesk.application.dtos.CreateDeviceDto;
import com.kristijan.iotdesk.application.dtos.DeviceDetailsDto;
import com.kristijan.iotdesk.application.dtos.DeviceDto;
import com.kristijan.iotdesk.application.exceptions.NotFoundException;
import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceChannelId;
import com.kristijan.iotdesk.domain.device.models.DeviceState;
import com.kristijan.iotdesk.domain.device.services.ChannelIdService;
import com.kristijan.iotdesk.domain.device.services.ListDevicesService;
import com.kristijan.iotdesk.domain.device.services.ManageDevicesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DevicesApplicationServiceTest {

  private DevicesApplicationService devicesApplicationService;

  @Mock
  private ListDevicesService listDevicesServiceMock;

  @Mock
  private ManageDevicesService manageDevicesServiceMock;

  @Mock
  private ChannelIdService channelIdServiceMock;

  private final Clock clock = Clock.systemUTC();

  @BeforeEach
  void setUp() {
    devicesApplicationService = new DevicesApplicationService(listDevicesServiceMock, manageDevicesServiceMock,
      channelIdServiceMock, clock);
  }

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
    when(manageDevicesServiceMock.createNewDevice(eq(dto1.getName()))).thenReturn(5L);
    CreateDeviceDto dto2 = new CreateDeviceDto("New Device 2");
    when(manageDevicesServiceMock.createNewDevice(eq(dto2.getName()))).thenReturn(6L);

    long result1 = devicesApplicationService.createNewDevice(dto1);
    long result2 = devicesApplicationService.createNewDevice(dto2);

    assertEquals(5L, result1);
    assertEquals(6L, result2);
  }

  @Test
  void shouldThrowWhenThereIsNoDeviceForGivenId() {
    assertThrows(NotFoundException.class, () -> devicesApplicationService.getDeviceById(1));
  }

  @Test
  void shouldFindDeviceByIdAndConvertToDeviceDetailsDto() {
    LocalDateTime now = LocalDateTime.now(clock);
    when(listDevicesServiceMock.findById(1))
      .thenReturn(Optional.of(createDevice(1L, "d1", now)));
    when(listDevicesServiceMock.findById(2))
      .thenReturn(Optional.of(createDevice(2L, "d2", now)));

    DeviceDetailsDto result1 = devicesApplicationService.getDeviceById(1);
    assertDeviceDetailsDto(result1, 1, "d1", DeviceState.NEW, ZonedDateTime.of(now, clock.getZone()));

    DeviceDetailsDto result2 = devicesApplicationService.getDeviceById(2);
    assertDeviceDetailsDto(result2, 2, "d2", DeviceState.NEW, ZonedDateTime.of(now, clock.getZone()));
  }

  void assertDeviceDto(DeviceDto dto, long id, String name, DeviceState state) {
    assertEquals(id, dto.getId());
    assertEquals(name, dto.getName());
    assertEquals(state, dto.getState());
  }

  void assertDeviceDetailsDto(DeviceDetailsDto dto, long id, String name, DeviceState state, ZonedDateTime createdAt) {
    assertDeviceDto(dto, id, name, state);
    assertEquals(createdAt, dto.getCreatedAt());
  }

  private Device createDevice(Long id, String d1, LocalDateTime createdAt) {
    Device device = new Device(d1, DeviceState.NEW);
    device.setId(id);
    device.setCreatedAt(createdAt);
    return device;
  }

  private Device createDevice(Long id, String name) {
    return createDevice(id, name, null);
  }

  @Test
  void shouldConvertDeviceChannelIdToDto() {
    DeviceChannelId deviceChannelId = new DeviceChannelId(2L, "channelId2");
    when(channelIdServiceMock.findByDeviceId(2L)).thenReturn(Optional.of(deviceChannelId));

    ChannelIdDto result = devicesApplicationService.getChannelIdForDevice(2L);

    assertEquals(deviceChannelId.getChannelId(), result.getChannelId());
  }

  @Test
  void shouldThrowIfChannelIdIsNotFound() {
    assertThrows(NotFoundException.class, () -> devicesApplicationService.getChannelIdForDevice(2L));
  }
}
