package com.kristijan.iotdesk.domain.device.models;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Device {

  @Setter
  private long id;

  private String name;
  private DeviceState state;

  public Device(String name, DeviceState state) {
    this.name = name;
    this.state = state;
  }
}
