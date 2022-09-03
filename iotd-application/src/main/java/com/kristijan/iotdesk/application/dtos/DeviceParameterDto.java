package com.kristijan.iotdesk.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class DeviceParameterDto {
  private long id;
  private int anchor;
  private String name;
}
