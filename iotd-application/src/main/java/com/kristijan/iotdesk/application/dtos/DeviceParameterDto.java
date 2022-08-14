package com.kristijan.iotdesk.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DeviceParameterDto {
  private long id;
  private int anchor;
  private String name;
}
