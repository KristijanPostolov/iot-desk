package com.kristijan.iotdesk.domain.device.services;

import com.kristijan.iotdesk.domain.device.exceptions.TransientDomainException;
import com.kristijan.iotdesk.domain.device.models.DeviceChannelId;
import com.kristijan.iotdesk.domain.device.ports.ChannelIdGenerator;
import com.kristijan.iotdesk.domain.device.repositories.DeviceChannelIdRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ChannelIdServiceTest {

  @InjectMocks
  private ChannelIdService channelIdService;

  @Mock
  private DeviceChannelIdRepository deviceChannelIdRepository;

  @Mock
  private ChannelIdGenerator channelIdGenerator;

  @Test
  void shouldCreateAndSaveDeviceChannelId() {
    when(channelIdGenerator.generate()).thenReturn("channelId-3");

    channelIdService.generateNewDeviceChannelId(3L);

    ArgumentCaptor<DeviceChannelId> deviceChannelIdCaptor = ArgumentCaptor.forClass(DeviceChannelId.class);
    verify(deviceChannelIdRepository).save(deviceChannelIdCaptor.capture());
    DeviceChannelId deviceChannelId = deviceChannelIdCaptor.getValue();
    assertEquals(3L, deviceChannelId.getDeviceId());
    assertEquals("channelId-3", deviceChannelId.getChannelId());
  }

  @Test
  void shouldThrowWhenGeneratedChannelIdIsBlank() {
    when(channelIdGenerator.generate()).thenReturn("");

    assertThrows(TransientDomainException.class, () -> channelIdService.generateNewDeviceChannelId(2L));
  }

  @Test
  void shouldReturnEmptyWhenDeviceIdDoesNotExist() {
    Optional<DeviceChannelId> result = channelIdService.findByDeviceId(2L);

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnDeviceChannelIdForGivenDeviceId() {
    when(deviceChannelIdRepository.findByDeviceId(2L))
      .thenReturn(Optional.of(new DeviceChannelId(2L, "channelId2")));

    Optional<DeviceChannelId> result = channelIdService.findByDeviceId(2L);

    assertTrue(result.isPresent());
    assertEquals(2L, result.get().getDeviceId());
    assertEquals("channelId2", result.get().getChannelId());
  }

  @Test
  void shouldReturnEmptyWhenChannelIdDoesNotExist() {
    Optional<DeviceChannelId> result = channelIdService.findByChannelId("nonExisting");

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnDeviceChannelIdForGivenChannelId() {
    when(deviceChannelIdRepository.findByChannelId("channelId3"))
      .thenReturn(Optional.of(new DeviceChannelId(3L, "channelId3")));

    Optional<DeviceChannelId> result = channelIdService.findByChannelId("channelId3");

    assertTrue(result.isPresent());
    assertEquals(3L, result.get().getDeviceId());
    assertEquals("channelId3", result.get().getChannelId());
  }

}