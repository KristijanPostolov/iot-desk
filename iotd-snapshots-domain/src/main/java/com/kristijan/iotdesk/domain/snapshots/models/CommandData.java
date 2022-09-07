package com.kristijan.iotdesk.domain.snapshots.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommandData {
  private final String commandId;
  private final String content;
}
