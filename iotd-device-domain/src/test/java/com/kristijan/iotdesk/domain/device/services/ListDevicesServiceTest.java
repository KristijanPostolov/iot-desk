package com.kristijan.iotdesk.domain.device.services;

import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceState;
import com.kristijan.iotdesk.domain.device.repositories.DevicesRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ListDevicesServiceTest {

  @InjectMocks
  private ListDevicesService listDevicesService;

  @Mock
  private DevicesRepository devicesRepositoryMock;

  @Test
  void shouldReturnEmptyWhenRepositoryReturnsNullList() {
    when(devicesRepositoryMock.findAll())
      .thenReturn(null);
    List<Device> devices = listDevicesService.getAllDevices();

    assertTrue(devices.isEmpty());
  }

  @Test
  void shouldReturnEmptyDeviceList() {
    when(devicesRepositoryMock.findAll())
      .thenReturn(Collections.emptyList());
    List<Device> devices = listDevicesService.getAllDevices();

    assertTrue(devices.isEmpty());
  }

  @Test
  void shouldReturnMultipleDevices() {
    List<Device> existingDevices =
      List.of(createDevice(1L, "d1"),
        createDevice(2L, "d2"),
        createDevice(3L, "d3"));
    when(devicesRepositoryMock.findAll())
      .thenReturn(existingDevices);
    List<Device> devices = listDevicesService.getAllDevices();

    assertEquals(3, devices.size());
    assertDeviceModel(devices.get(0), 1L, "d1");
    assertDeviceModel(devices.get(1), 2L, "d2");
    assertDeviceModel(devices.get(2), 3L, "d3");
  }

  @Test
  void shouldReturnEmptyWhenDeviceWithGivenIdDoesNotExist() {
    Optional<Device> result = listDevicesService.findById(1);
    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnDeviceForGivenId() {
    Device existingDevice = createDevice(2L, "d2", LocalDateTime.MIN);
    when(devicesRepositoryMock.findById(2)).thenReturn(Optional.of(existingDevice));

    Optional<Device> result = listDevicesService.findById(2);

    assertTrue(result.isPresent());
    assertDeviceModel(result.get(), 2L, "d2");
    assertEquals(LocalDateTime.MIN, result.get().getCreatedAt());
  }

  @Test
  void shouldFetchCorrectDeviceFromRepository() {
    Device existingDevice1 = createDevice(1L, "d1");
    when(devicesRepositoryMock.findById(1)).thenReturn(Optional.of(existingDevice1));
    Device existingDevice2 = createDevice(2L, "d2");
    when(devicesRepositoryMock.findById(2)).thenReturn(Optional.of(existingDevice2));

    Optional<Device> result = listDevicesService.findById(1);
    assertTrue(result.isPresent());
    assertEquals("d1", result.get().getName());
    result = listDevicesService.findById(2L);
    assertTrue(result.isPresent());
    assertEquals("d2", result.get().getName());
  }

  void assertDeviceModel(Device device, Long id, String name) {
    assertEquals(id, device.getId());
    assertEquals(name, device.getName());
    assertEquals(DeviceState.NEW, device.getState());
  }

  private Device createDevice(Long id, String name) {
    return createDevice(id, name, null);
  }

  private Device createDevice(Long id, String name, LocalDateTime createdAt) {
    Device device = new Device(name, DeviceState.NEW);
    device.setId(id);
    device.setCreatedAt(createdAt);
    return device;
  }

}

