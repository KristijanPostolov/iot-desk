package com.kristijan.iotdesk.domain.snapshots.models;

import lombok.Value;

@Value
public class CommandAck {
  String commandId;
  boolean successful;
}
