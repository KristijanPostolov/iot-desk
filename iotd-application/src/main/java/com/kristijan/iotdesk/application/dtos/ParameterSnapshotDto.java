package com.kristijan.iotdesk.application.dtos;

import lombok.Value;

import java.time.ZonedDateTime;

@Value
public class ParameterSnapshotDto {
  ZonedDateTime timestamp;
  double value;
}
