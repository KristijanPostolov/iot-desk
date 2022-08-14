package com.kristijan.iotdesk.messaging.mqtt.models;

import com.kristijan.iotdesk.domain.snapshots.models.AnchorSnapshot;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ParsingResult {

  private final boolean valid;
  private final List<AnchorSnapshot> anchorSnapshots;

  public static ParsingResult invalid() {
    return new ParsingResult(false, null);
  }

  public static ParsingResult of(List<AnchorSnapshot> anchorSnapshots) {
    return new ParsingResult(true, anchorSnapshots);
  }
}
