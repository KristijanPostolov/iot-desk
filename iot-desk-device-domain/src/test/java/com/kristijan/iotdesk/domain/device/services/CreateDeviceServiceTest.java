package com.kristijan.iotdesk.domain.device.services;

import com.kristijan.iotdesk.domain.device.exceptions.DomainException;
import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceState;
import com.kristijan.iotdesk.domain.device.repositories.DevicesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateDeviceServiceTest {

  @InjectMocks
  private CreateDeviceService createDeviceService;

  @Mock
  private DevicesRepository devicesRepository;

  @Test
  void shouldThrowWhenDeviceNameIsNull() {
    assertThrows(DomainException.class, () -> createDeviceService.createNewDevice(null));
  }

  @Test
  void shouldThrowWhenDeviceNameIsBlank() {
    assertThrows(DomainException.class, () -> createDeviceService.createNewDevice(" "));
  }

  @Test
  void shouldCreateNewDevice() {
    when(devicesRepository.save(any())).thenReturn(1L);

    long id = createDeviceService.createNewDevice("device1");

    assertEquals(1L, id);
    ArgumentCaptor<Device> deviceCaptor = ArgumentCaptor.forClass(Device.class);
    verify(devicesRepository).save(deviceCaptor.capture());
    Device newDevice = deviceCaptor.getValue();
    assertNotNull(newDevice);
    assertEquals("device1", newDevice.getName());
    assertEquals(DeviceState.NEW, newDevice.getState());
  }

}
