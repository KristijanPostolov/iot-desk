package com.kristijan.iotdesk.domain.snapshots.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@RequiredArgsConstructor
public class ParameterSnapshot {
  @Setter
  private Long id;

  private final long parameterId;
  private final ZonedDateTime timestamp;
  private final Double value;
}
