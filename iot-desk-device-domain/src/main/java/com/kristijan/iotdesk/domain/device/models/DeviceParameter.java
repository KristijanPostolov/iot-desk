package com.kristijan.iotdesk.domain.device.models;

import lombok.Getter;
import lombok.Setter;

@Getter
public class DeviceParameter {

  @Setter
  private Long id;
  private final Long deviceId;
  private final int anchor;

  private String name;

  public DeviceParameter(long deviceId, int anchor, String name) {
    this.deviceId = deviceId;
    this.anchor = anchor;
    this.name = name;
  }
}
