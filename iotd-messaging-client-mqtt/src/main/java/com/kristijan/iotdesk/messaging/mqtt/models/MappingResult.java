package com.kristijan.iotdesk.messaging.mqtt.models;

import com.kristijan.iotdesk.domain.snapshots.models.AnchorSnapshot;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MappingResult {

  private final boolean valid;
  private final List<AnchorSnapshot> anchorSnapshots;

  public static MappingResult invalid() {
    return new MappingResult(false, null);
  }

  public static MappingResult of(List<AnchorSnapshot> anchorSnapshots) {
    return new MappingResult(true, anchorSnapshots);
  }
}
