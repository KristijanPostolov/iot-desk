package com.kristijan.iotdesk.domain.device.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class Device {

  @Setter
  private long id;

  private String name;
  private DeviceState state;

  @Setter
  private LocalDateTime createdAt;

  public Device(String name, DeviceState state) {
    this.name = name;
    this.state = state;
  }
}
