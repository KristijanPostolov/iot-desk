package com.kristijan.iotdesk.domain.device.services;

import com.kristijan.iotdesk.domain.device.exceptions.DomainException;
import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceParameter;
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
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ManageDevicesServiceTest {

  private ManageDevicesService manageDevicesService;

  @Mock
  private DevicesRepository devicesRepository;

  @Mock
  private ChannelIdService channelIdService;

  private final Clock fixedClock = Clock.fixed(Instant.parse("2022-07-24T16:00:00Z"), ZoneOffset.UTC);

  @BeforeEach
  void setUp() {
    manageDevicesService = new ManageDevicesService(devicesRepository, channelIdService, fixedClock);
  }

  @Test
  void shouldThrowWhenDeviceNameIsNull() {
    assertThrows(DomainException.class, () -> manageDevicesService.createNewDevice(null));
  }

  @Test
  void shouldThrowWhenDeviceNameIsBlank() {
    assertThrows(DomainException.class, () -> manageDevicesService.createNewDevice(" "));
  }

  @Test
  void shouldCreateNewDevice() {
    when(devicesRepository.save(any())).thenReturn(1L);

    long id = manageDevicesService.createNewDevice("device1");

    assertEquals(1L, id);
    ArgumentCaptor<Device> deviceCaptor = ArgumentCaptor.forClass(Device.class);
    verify(devicesRepository).save(deviceCaptor.capture());
    Device newDevice = deviceCaptor.getValue();
    assertNotNull(newDevice);
    assertEquals("device1", newDevice.getName());
    assertEquals(DeviceState.NEW, newDevice.getState());
    assertEquals(LocalDateTime.now(fixedClock), newDevice.getCreatedAt());
    verify(channelIdService).generateNewDeviceChannelId(1L);
  }

  @Test
  void shouldThrowIfActivatingNonExistingDevice() {
    assertThrows(DomainException.class, () -> manageDevicesService.activateDevice(1L));
  }

  @Test
  void shouldThrowIfActivatingDeviceThatIsNotNew() {
    when(devicesRepository.findById(1L)).thenReturn(Optional.of(new Device("testDevice", DeviceState.ACTIVE)));

    assertThrows(DomainException.class, () -> manageDevicesService.activateDevice(1L));

    verify(devicesRepository, times(0)).updateStatus(any());
  }

  @Test
  void shouldActivateDevice() {
    when(devicesRepository.findById(1L)).thenReturn(Optional.of(new Device("testDevice", DeviceState.NEW)));
    Device updatedDevice = new Device("testDevice", DeviceState.ACTIVE);
    when(devicesRepository.updateStatus(any())).thenReturn(updatedDevice);

    Device device = manageDevicesService.activateDevice(1L);

    assertEquals(updatedDevice, device);
    ArgumentCaptor<Device> deviceCaptor = ArgumentCaptor.forClass(Device.class);
    verify(devicesRepository).updateStatus(deviceCaptor.capture());
    assertEquals(DeviceState.ACTIVE, deviceCaptor.getValue().getState());
  }

  @Test
  void shouldThrowIfCreatingParametersForNonExistingDevice() {
    assertThrows(DomainException.class, () -> manageDevicesService.updateDeviceParameters(1L, Set.of(1)));
  }

  @Test
  void shouldCreateDeviceParameters() {
    final Device deviceBefore = new Device("testDevice", DeviceState.ACTIVE);
    deviceBefore.setId(1L);
    when(devicesRepository.findById(1L)).thenReturn(Optional.of(deviceBefore));
    Device updatedDevice = mock(Device.class);
    when(devicesRepository.saveParameters(any(), any())).thenReturn(updatedDevice);

    Device device = manageDevicesService.updateDeviceParameters(1L, Set.of(1, 2, 3));

    assertEquals(updatedDevice, device);
    ArgumentCaptor<Set<DeviceParameter>> parametersCaptor = ArgumentCaptor.forClass(Set.class);
    verify(devicesRepository).saveParameters(eq(deviceBefore), parametersCaptor.capture());
    Set<DeviceParameter> parameters = parametersCaptor.getValue();
    assertEquals(3, parameters.size());
    assertDeviceParameter(parameters.stream().filter(parameter -> 1 == parameter.getAnchor()).findFirst().orElse(null),
      1L, 1, "Parameter 1");
    assertDeviceParameter(parameters.stream().filter(parameter -> 2 == parameter.getAnchor()).findFirst().orElse(null),
      1L, 2, "Parameter 2");
    assertDeviceParameter(parameters.stream().filter(parameter -> 3 == parameter.getAnchor()).findFirst().orElse(null),
      1L, 3, "Parameter 3");
  }

  @Test
  void shouldCreateOnlyNewDeviceParameters() {
    Device deviceBefore = new Device("testDevice", DeviceState.ACTIVE);
    deviceBefore.setId(2L);
    deviceBefore.getParameters().add(new DeviceParameter(2L, 1, "1"));
    when(devicesRepository.findById(2L)).thenReturn(Optional.of(deviceBefore));
    Device updatedDevice = mock(Device.class);
    when(devicesRepository.saveParameters(any(), any())).thenReturn(updatedDevice);

    Device device = manageDevicesService.updateDeviceParameters(2L, Set.of(1, 2));

    assertEquals(updatedDevice, device);
    ArgumentCaptor<Set<DeviceParameter>> parametersCaptor = ArgumentCaptor.forClass(Set.class);
    verify(devicesRepository).saveParameters(any(), parametersCaptor.capture());
    Set<DeviceParameter> parameters = parametersCaptor.getValue();
    assertEquals(1, parameters.size());
    assertDeviceParameter(parameters.stream().findFirst().orElse(null),
      2L, 2, "Parameter 2");
  }

  @Test
  void shouldNotSaveParametersWhenThereAreNoneToAdd() {
    final Device deviceBefore = new Device("testDevice", DeviceState.ACTIVE);
    deviceBefore.setId(2L);
    deviceBefore.getParameters().add(new DeviceParameter(2L, 1, "1"));
    when(devicesRepository.findById(2L)).thenReturn(Optional.of(deviceBefore));

    Device device = manageDevicesService.updateDeviceParameters(2L, Set.of(1));

    assertEquals(deviceBefore, device);
    verify(devicesRepository, times(0)).saveParameters(any(), any());
  }

  @Test
  void shouldThrowIfRenamingParameterForNonExistingDevice() {
    assertThrows(DomainException.class, () -> manageDevicesService.renameDeviceParameter(1L, 1, "Name"));
  }

  @Test
  void shouldThrowIfRenamingParameterForNonExistingAnchorForDevice() {
    Device device = new Device("Device 2", DeviceState.ACTIVE);
    device.getParameters().add(new DeviceParameter(2L, 1, "Parameter 1"));
    when(devicesRepository.findById(2L)).thenReturn(Optional.of(device));

    assertThrows(DomainException.class, () -> manageDevicesService.renameDeviceParameter(2L, 3, "Name"));
  }

  @Test
  void shouldUpdateNameOfExistingParameter() {
    Device device = new Device("Device 2", DeviceState.ACTIVE);
    device.getParameters().add(new DeviceParameter(2L, 1, "Parameter 1"));
    when(devicesRepository.findById(2L)).thenReturn(Optional.of(device));

    manageDevicesService.renameDeviceParameter(2L, 1, "New name");

    ArgumentCaptor<Device> deviceCaptor = ArgumentCaptor.forClass(Device.class);
    verify(devicesRepository).updateParameters(deviceCaptor.capture());
    Device savedDevice = deviceCaptor.getValue();
    assertEquals(1, savedDevice.getParameters().size());
    DeviceParameter savedParameter = savedDevice.getParameters().stream().findFirst().orElse(null);
    assertNotNull(savedParameter);
    assertEquals(1, savedParameter.getAnchor());
    assertEquals("New name", savedParameter.getName());
  }

  private void assertDeviceParameter(DeviceParameter parameter, long expectedDeviceId, int expectedAnchor,
                                     String expectedName) {
    assertNotNull(parameter);
    assertEquals(expectedDeviceId, parameter.getDeviceId());
    assertEquals(expectedAnchor, parameter.getAnchor());
    assertEquals(expectedName, parameter.getName());
  }
}
