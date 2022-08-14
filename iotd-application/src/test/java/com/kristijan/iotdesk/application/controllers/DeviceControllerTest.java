package com.kristijan.iotdesk.application.controllers;

import com.kristijan.iotdesk.application.dtos.ChannelIdDto;
import com.kristijan.iotdesk.application.dtos.CreateDeviceDto;
import com.kristijan.iotdesk.application.dtos.DeviceDetailsDto;
import com.kristijan.iotdesk.application.dtos.DeviceDto;
import com.kristijan.iotdesk.application.services.DevicesApplicationService;
import com.kristijan.iotdesk.domain.device.models.DeviceState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class DeviceControllerTest {

  @InjectMocks
  private DeviceController deviceController;

  @Mock
  private DevicesApplicationService devicesApplicationServiceMock;

  @Test
  void shouldListAllDevices() {
    List<DeviceDto> allDevices = List.of(new DeviceDto(1L, "d1", DeviceState.NEW));
    when(devicesApplicationServiceMock.getAllDevices()).thenReturn(allDevices);

    List<DeviceDto> result = deviceController.getAllDevices();

    assertEquals(allDevices, result);
  }

  @Test
  void shouldCreateDevice() {
    CreateDeviceDto request = new CreateDeviceDto("New device");

    deviceController.createDevice(request);

    verify(devicesApplicationServiceMock).createNewDevice(request);
  }

  @Test
  void shouldFindDeviceById() {
    DeviceDetailsDto dto = new DeviceDetailsDto(1L, "d1", DeviceState.NEW, ZonedDateTime.now(),
      Collections.emptyList());
    when(devicesApplicationServiceMock.getDeviceById(1)).thenReturn(dto);

    DeviceDetailsDto result = deviceController.getDeviceById(1L);

    assertEquals(dto, result);
    verify(devicesApplicationServiceMock).getDeviceById(1);
  }

  @Test
  void shouldGetChannelIdForDevice() {
    ChannelIdDto dto = new ChannelIdDto("channelId2");
    when(devicesApplicationServiceMock.getChannelIdForDevice(2L)).thenReturn(dto);

    ChannelIdDto result = deviceController.getChannelIdForDevice(2L);

    assertEquals(dto, result);
    verify(devicesApplicationServiceMock).getChannelIdForDevice(2L);
  }
}
