package com.kristijan.iotdesk.domain.snapshots.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum AcknowledgementStatus {
  NO_ACK((short) 1),
  ACK_SUCCESSFUL((short) 2),
  ACK_FAILED((short) 3);

  private final short id;

  public static AcknowledgementStatus fromId(short id) {
    return Arrays.stream(values())
      .filter(state -> state.id == id)
      .findFirst()
      .orElseThrow(() -> new IllegalArgumentException("Invalid id for AcknowledgementStatus"));
  }
}
