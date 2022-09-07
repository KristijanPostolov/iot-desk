package com.kristijan.iotdesk.domain.snapshots.services;

import com.kristijan.iotdesk.domain.device.exceptions.DomainException;
import com.kristijan.iotdesk.domain.device.exceptions.TransientDomainException;
import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceChannelId;
import com.kristijan.iotdesk.domain.device.models.DeviceState;
import com.kristijan.iotdesk.domain.device.services.ChannelIdService;
import com.kristijan.iotdesk.domain.device.services.ListDevicesService;
import com.kristijan.iotdesk.domain.snapshots.models.AcknowledgementStatus;
import com.kristijan.iotdesk.domain.snapshots.models.CommandData;
import com.kristijan.iotdesk.domain.snapshots.models.CommandRequest;
import com.kristijan.iotdesk.domain.snapshots.models.DeviceCommand;
import com.kristijan.iotdesk.domain.snapshots.ports.DeviceCommandSender;
import com.kristijan.iotdesk.domain.snapshots.repositories.DeviceCommandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceCommandServiceTest {

  private DeviceCommandService deviceCommandService;

  @Mock
  private ListDevicesService listDevicesService;
  @Mock
  private ChannelIdService channelIdService;
  @Mock
  private DeviceCommandSender deviceCommandSender;
  @Mock
  private DeviceCommandRepository deviceCommandRepository;

  private final LocalDateTime now = LocalDateTime.parse("2022-09-07T21:32:00");
  private final Clock clock = Clock.fixed(now.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);

  @BeforeEach
  void setUp() {
    deviceCommandService = new DeviceCommandService(listDevicesService, channelIdService, deviceCommandSender,
      deviceCommandRepository, clock);
  }

  @Test
  void shouldThrowOnNonExistingDevice() {
    assertThrows(DomainException.class, () ->
      deviceCommandService.postDeviceCommand(new CommandRequest(1L, "content")));

    verify(deviceCommandSender, times(0)).sendCommandToDevice(any(), any());
    verify(deviceCommandRepository, times(0)).save(any());
  }

  @Test
  void shouldThrowWhenDeviceStateIsNotActive() {
    when(listDevicesService.findById(1L)).thenReturn(Optional.of(new Device("Device 1", DeviceState.NEW)));

    assertThrows(DomainException.class, () ->
      deviceCommandService.postDeviceCommand(new CommandRequest(1L, "content")));

    verify(deviceCommandSender, times(0)).sendCommandToDevice(any(), any());
    verify(deviceCommandRepository, times(0)).save(any());
  }

  @Test
  void shouldThrowWhenDeviceDoesNotHaveChannelId() {
    when(listDevicesService.findById(1L)).thenReturn(Optional.of(new Device("Device 1", DeviceState.ACTIVE)));

    assertThrows(DomainException.class, () ->
      deviceCommandService.postDeviceCommand(new CommandRequest(1L, "content")));

    verify(deviceCommandSender, times(0)).sendCommandToDevice(any(), any());
    verify(deviceCommandRepository, times(0)).save(any());
  }

  @Test
  void shouldWrapTransientDomainExceptionWhenSendingFails() {
    when(listDevicesService.findById(1L)).thenReturn(Optional.of(new Device("Device 1", DeviceState.ACTIVE)));
    when(channelIdService.findByDeviceId(1L)).thenReturn(Optional.of(new DeviceChannelId(2L, "channelId")));
    doThrow(new RuntimeException()).when(deviceCommandSender).sendCommandToDevice(eq("channelId"), any());

    assertThrows(TransientDomainException.class, () ->
      deviceCommandService.postDeviceCommand(new CommandRequest(1L, "content")));

    verify(deviceCommandRepository, times(0)).save(any());
  }

  @Test
  void shouldSendDeviceCommand() {
    when(listDevicesService.findById(2L)).thenReturn(Optional.of(new Device("Device 1", DeviceState.ACTIVE)));
    when(channelIdService.findByDeviceId(2L)).thenReturn(Optional.of(new DeviceChannelId(2L, "channelId")));

    deviceCommandService.postDeviceCommand(new CommandRequest(2L,"commandContent"));

    ArgumentCaptor<CommandData> commandDataCaptor = ArgumentCaptor.forClass(CommandData.class);
    verify(deviceCommandSender).sendCommandToDevice(eq("channelId"), commandDataCaptor.capture());
    CommandData request = commandDataCaptor.getValue();
    assertNotNull(request.getCommandId());
    assertEquals(UUID.randomUUID().toString().length(), request.getCommandId().length());
    assertEquals("commandContent", request.getContent());
  }

  @Test
  void shouldSaveCommandInRepository() {
    when(listDevicesService.findById(2L)).thenReturn(Optional.of(new Device("Device 1", DeviceState.ACTIVE)));
    when(channelIdService.findByDeviceId(2L)).thenReturn(Optional.of(new DeviceChannelId(2L, "channelId")));

    deviceCommandService.postDeviceCommand(new CommandRequest(2L,"commandContent"));

    ArgumentCaptor<DeviceCommand> deviceCommandCaptor = ArgumentCaptor.forClass(DeviceCommand.class);
    verify(deviceCommandRepository).save(deviceCommandCaptor.capture());
    DeviceCommand deviceCommand = deviceCommandCaptor.getValue();
    assertNotNull(deviceCommand.getCommandId());
    assertEquals(UUID.randomUUID().toString().length(), deviceCommand.getCommandId().length());
    assertEquals("commandContent", deviceCommand.getContent());
    assertEquals(2L, deviceCommand.getDeviceId());
    assertEquals(now, deviceCommand.getSentAt());
    assertEquals(AcknowledgementStatus.NO_ACK, deviceCommand.getAckStatus());
    assertNull(deviceCommand.getAcknowledgedAt());
  }
}