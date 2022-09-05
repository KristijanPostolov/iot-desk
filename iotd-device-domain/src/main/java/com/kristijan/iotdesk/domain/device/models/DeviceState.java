package com.kristijan.iotdesk.domain.device.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

/**
 * Device state values.
 */
@RequiredArgsConstructor
@Getter
public enum DeviceState {
  NEW((short) 1),
  ACTIVE((short) 2);

  private final short id;

  public static DeviceState fromId(short id) {
    return Arrays.stream(values())
      .filter(state -> state.id == id)
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Invalid if for DeviceState"));
  }
}
