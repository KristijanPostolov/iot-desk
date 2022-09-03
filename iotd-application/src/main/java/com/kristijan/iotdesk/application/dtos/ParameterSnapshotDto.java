package com.kristijan.iotdesk.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ParameterSnapshotDto {
  private ZonedDateTime timestamp;
  private double value;
}
