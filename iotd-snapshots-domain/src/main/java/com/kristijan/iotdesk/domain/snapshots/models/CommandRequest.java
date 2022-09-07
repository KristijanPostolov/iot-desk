package com.kristijan.iotdesk.domain.snapshots.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommandRequest {
  private final Long deviceId;
  private final String content;
}
