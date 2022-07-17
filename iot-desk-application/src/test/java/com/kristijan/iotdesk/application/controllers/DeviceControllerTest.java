package com.kristijan.iotdesk.application.controllers;

import com.kristijan.iotdesk.application.dtos.DeviceDto;
import com.kristijan.iotdesk.application.services.DevicesApplicationService;
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
    List<DeviceDto> allDevices = List.of(new DeviceDto("d1"));
    when(devicesApplicationServiceMock.getAllDevices()).thenReturn(allDevices);

    List<DeviceDto> result = deviceController.getAllDevices();

    assertEquals(allDevices, result);
  }
}
