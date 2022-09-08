package com.kristijan.iotdesk.messaging.mqtt.models;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ParsingResult<T> {

  private final boolean valid;
  private final T result;

  public static <T> ParsingResult<T> invalid() {
    return new ParsingResult<>(false, null);
  }

  public static <T> ParsingResult<T> of(T anchorSnapshots) {
    return new ParsingResult<>(true, anchorSnapshots);
  }
}
