package com.kristijan.iotdesk.domain.device.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


class DeviceStateTest {

  @Test
  void shouldReturnDeviceStateById() {
    assertEquals(DeviceState.NEW, DeviceState.fromId((short) 1));
    assertEquals(DeviceState.ACTIVE, DeviceState.fromId((short) 2));
  }

  @Test
  void shouldThrowForNonExistingId() {
    assertThrows(IllegalArgumentException.class, () -> DeviceState.fromId((short) -1));
  }
}