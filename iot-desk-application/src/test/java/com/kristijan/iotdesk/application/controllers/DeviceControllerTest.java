package com.kristijan.iotdesk.application.controllers;

import com.kristijan.iotdesk.application.dtos.DeviceDto;
import com.kristijan.iotdesk.application.services.DevicesApplicationService;
import com.kristijan.iotdesk.domain.device.models.DeviceState;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
}
