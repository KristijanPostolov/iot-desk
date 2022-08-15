package com.kristijan.iotdesk.domain.snapshots.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@ExtendWith(MockitoExtension.class)
class DeviceMessagingErrorHandlerTest {

  @InjectMocks
  private DeviceMessagingErrorHandler deviceMessagingErrorHandler;

  @Test
  void shouldHandleErrorsWithoutThrowing() {
    assertDoesNotThrow(() -> deviceMessagingErrorHandler.nonExistingChannelId("channelId"));
    assertDoesNotThrow(() -> deviceMessagingErrorHandler.nonExistingDevice(1));
    assertDoesNotThrow(() -> deviceMessagingErrorHandler.invalidPayload("channelId"));
    assertDoesNotThrow(() -> deviceMessagingErrorHandler.handleDomainException("channelId", new RuntimeException()));
  }
}