package com.kristijan.iotdesk.application.services;

import com.kristijan.iotdesk.application.dtos.ChannelIdDto;
import com.kristijan.iotdesk.application.dtos.CreateDeviceDto;
import com.kristijan.iotdesk.application.dtos.CreateDeviceResponseDto;
import com.kristijan.iotdesk.application.dtos.DeviceDetailsDto;
import com.kristijan.iotdesk.application.dtos.DeviceDto;
import com.kristijan.iotdesk.application.dtos.DeviceParameterDto;
import com.kristijan.iotdesk.application.dtos.ParameterRenameDto;
import com.kristijan.iotdesk.application.exceptions.NotFoundException;
import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceChannelId;
import com.kristijan.iotdesk.domain.device.models.DeviceParameter;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
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
      List.of(createDevice(1L, "d1", DeviceState.NEW, null),
        createDevice(2L, "d2", DeviceState.ACTIVE, null),
        createDevice(3L, "d3", DeviceState.NEW, null));
    when(listDevicesServiceMock.getAllDevices()).thenReturn(existingDevices);
    List<DeviceDto> result = devicesApplicationService.getAllDevices();

    assertEquals(3, result.size());
    assertDeviceDto(result.get(0), 1, "d1", DeviceState.NEW);
    assertDeviceDto(result.get(1), 2, "d2", DeviceState.ACTIVE);
    assertDeviceDto(result.get(2), 3, "d3", DeviceState.NEW);
  }

  @Test
  void shouldReturnIdForEachDevice() {
    CreateDeviceDto dto1 = new CreateDeviceDto("New Device 1");
    when(manageDevicesServiceMock.createNewDevice(eq(dto1.getName()))).thenReturn(5L);
    CreateDeviceDto dto2 = new CreateDeviceDto("New Device 2");
    when(manageDevicesServiceMock.createNewDevice(eq(dto2.getName()))).thenReturn(6L);

    CreateDeviceResponseDto result1 = devicesApplicationService.createNewDevice(dto1);
    CreateDeviceResponseDto result2 = devicesApplicationService.createNewDevice(dto2);

    assertEquals(5L, result1.getId());
    assertEquals(6L, result2.getId());
  }

  @Test
  void shouldThrowWhenThereIsNoDeviceForGivenId() {
    assertThrows(NotFoundException.class, () -> devicesApplicationService.getDeviceById(1));
  }

  @Test
  void shouldFindDeviceByIdAndConvertToDeviceDetailsDto() {
    LocalDateTime now = LocalDateTime.now(clock);
    Device device = createDevice(2L, "d2", DeviceState.ACTIVE, now);
    device.getParameters().add(createDeviceParameter(11, 2L, 1, "Param B"));
    device.getParameters().add(createDeviceParameter(12, 2L, 2, "Param A"));
    device.getParameters().add(createDeviceParameter(13, 2L, 3, "Param C"));
    when(listDevicesServiceMock.findById(2)).thenReturn(Optional.of(device));

    DeviceDetailsDto result = devicesApplicationService.getDeviceById(2);

    assertDeviceDto(result, 2, "d2", DeviceState.ACTIVE);
    assertEquals(ZonedDateTime.of(now, clock.getZone()), result.getCreatedAt());
    List<DeviceParameterDto> deviceParameters = result.getParameters();
    assertEquals(3, deviceParameters.size());
    assertDeviceParameterDto(deviceParameters.get(0), 12, 2, "Param A");
    assertDeviceParameterDto(deviceParameters.get(1), 11, 1, "Param B");
    assertDeviceParameterDto(deviceParameters.get(2), 13, 3, "Param C");
  }

  void assertDeviceDto(DeviceDto dto, long id, String name, DeviceState state) {
    assertEquals(id, dto.getId());
    assertEquals(name, dto.getName());
    assertEquals(state, dto.getState());
  }

  void assertDeviceParameterDto(DeviceParameterDto dto, long expectedId, int expectedAnchor, String expectedName) {
    assertNotNull(dto);
    assertEquals(expectedId, dto.getId());
    assertEquals(expectedAnchor, dto.getAnchor());
    assertEquals(expectedName, dto.getName());
  }

  private Device createDevice(Long id, String name, DeviceState state, LocalDateTime createdAt) {
    Device device = new Device(name, state);
    device.setId(id);
    device.setCreatedAt(createdAt);
    return device;
  }

  private DeviceParameter createDeviceParameter(long id, long deviceId, int anchor, String name) {
    DeviceParameter parameter = new DeviceParameter(deviceId, anchor, name);
    parameter.setId(id);
    return parameter;
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

  @Test
  void shouldRenameParameter() {
    devicesApplicationService.renameDeviceParameter(1L, 2, new ParameterRenameDto("New name"));

    verify(manageDevicesServiceMock).renameDeviceParameter(1L, 2, "New name");
  }
}
