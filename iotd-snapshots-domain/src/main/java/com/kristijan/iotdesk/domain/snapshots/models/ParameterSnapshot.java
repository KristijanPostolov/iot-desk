package com.kristijan.iotdesk.domain.snapshots.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public class ParameterSnapshot {
  @Setter
  private Long id;

  private final long parameterId;
  private final LocalDateTime timestamp;
  private final Double value;
}
