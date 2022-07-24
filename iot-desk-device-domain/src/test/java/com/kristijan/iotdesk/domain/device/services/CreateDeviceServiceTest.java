package com.kristijan.iotdesk.domain.device.services;

import com.kristijan.iotdesk.domain.device.exceptions.DomainException;
import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceState;
import com.kristijan.iotdesk.domain.device.repositories.DevicesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CreateDeviceServiceTest {

  private CreateDeviceService createDeviceService;

  @Mock
  private DevicesRepository devicesRepository;

  private final Clock fixedClock = Clock.fixed(Instant.parse("2022-07-24T16:00:00Z"), ZoneOffset.UTC);

  @BeforeEach
  void setUp() {
    createDeviceService = new CreateDeviceService(devicesRepository, fixedClock);
  }

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
    assertEquals(LocalDateTime.now(fixedClock), newDevice.getCreatedAt());
  }

}
