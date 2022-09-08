package com.kristijan.iotdesk.domain.snapshots.services;

import com.kristijan.iotdesk.domain.device.models.DeviceChannelId;
import com.kristijan.iotdesk.domain.device.services.ChannelIdService;
import com.kristijan.iotdesk.domain.snapshots.models.AcknowledgementStatus;
import com.kristijan.iotdesk.domain.snapshots.models.CommandAck;
import com.kristijan.iotdesk.domain.snapshots.models.DeviceCommand;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceCommandAckServiceTest {

  private DeviceCommandAckService deviceCommandAckService;

  @Mock
  private ChannelIdService channelIdService;
  @Mock
  private DeviceCommandRepository deviceCommandRepository;
  @Mock
  private DeviceMessagingErrorHandler deviceMessagingErrorHandler;

  private final LocalDateTime now = LocalDateTime.parse("2022-09-08T23:03:00");
  private final Clock clock = Clock.fixed(now.toInstant(ZoneOffset.UTC), ZoneOffset.UTC);

  @BeforeEach
  void setUp() {
    deviceCommandAckService = new DeviceCommandAckService(channelIdService, deviceMessagingErrorHandler,
      deviceCommandRepository, clock);
  }

  @Test
  void shouldHandleErrorInCaseOfNonExistingChannelId() {
    boolean result = deviceCommandAckService.acknowledgeCommand("invalid",
      new CommandAck("commandId", true));

    assertFalse(result);
    verify(deviceMessagingErrorHandler).nonExistingChannelId("invalid");
  }

  @Test
  void shouldHandleErrorForNonExistingCommand() {
    when(channelIdService.findByChannelId("channelId"))
      .thenReturn(Optional.of(new DeviceChannelId(1L, "channelId")));

    boolean result = deviceCommandAckService.acknowledgeCommand("channelId",
      new CommandAck("commandId", true));

    assertFalse(result);
    verify(deviceMessagingErrorHandler).ackForNonExistingDeviceCommand("commandId", "channelId");
  }

  @Test
  void shouldHandleErrorWhenAcknowledgementIsSendByDifferentDevice() {
    when(channelIdService.findByChannelId("channelId"))
      .thenReturn(Optional.of(new DeviceChannelId(1L, "channelId")));
    when(deviceCommandRepository.findByCommandId("commandId")).thenReturn(Optional.of(createCommand(2L)));

    boolean result = deviceCommandAckService.acknowledgeCommand("channelId",
      new CommandAck("commandId", true));

    assertFalse(result);
    verify(deviceMessagingErrorHandler).ackReceivedForExistingCommandOnDifferentChannel("commandId", "channelId");
  }

  @Test
  void shouldHandleSuccessfulAcknowledgement() {
    when(channelIdService.findByChannelId("channelId"))
      .thenReturn(Optional.of(new DeviceChannelId(1L, "channelId")));
    when(deviceCommandRepository.findByCommandId("commandId")).thenReturn(Optional.of(createCommand(1L)));

    boolean result = deviceCommandAckService.acknowledgeCommand("channelId",
      new CommandAck("commandId", true));

    assertTrue(result);
    ArgumentCaptor<DeviceCommand> deviceCommandCaptor = ArgumentCaptor.forClass(DeviceCommand.class);
    verify(deviceCommandRepository).save(deviceCommandCaptor.capture());
    DeviceCommand saved = deviceCommandCaptor.getValue();
    assertEquals(AcknowledgementStatus.ACK_SUCCESSFUL, saved.getAckStatus());
    assertEquals(now, saved.getAcknowledgedAt());
  }

  @Test
  void shouldHandleFailedAcknowledgement() {
    when(channelIdService.findByChannelId("channelId"))
      .thenReturn(Optional.of(new DeviceChannelId(1L, "channelId")));
    when(deviceCommandRepository.findByCommandId("commandId")).thenReturn(Optional.of(createCommand(1L)));

    boolean result = deviceCommandAckService.acknowledgeCommand("channelId",
      new CommandAck("commandId", false));

    assertTrue(result);
    ArgumentCaptor<DeviceCommand> deviceCommandCaptor = ArgumentCaptor.forClass(DeviceCommand.class);
    verify(deviceCommandRepository).save(deviceCommandCaptor.capture());
    DeviceCommand saved = deviceCommandCaptor.getValue();
    assertEquals(AcknowledgementStatus.ACK_FAILED, saved.getAckStatus());
    assertEquals(now, saved.getAcknowledgedAt());
  }

  private DeviceCommand createCommand(long deviceId) {
    return new DeviceCommand("commandId", "content", deviceId, now.minusMinutes(2),
      AcknowledgementStatus.NO_ACK);
  }
}