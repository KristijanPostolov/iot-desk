package com.kristijan.iotdesk.domain.snapshots.models;

import lombok.Value;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class DeviceSnapshot {
  String channelId;
  LocalDateTime timestamp;
  List<AnchorSnapshot> anchorSnapshots;
}
