package com.kristijan.iotdesk.domain.device.models;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Device domain model.
 */
@Getter
public class Device {

  @Setter
  private Long id;

  private String name;

  @Setter
  private DeviceState state;

  @Setter
  private LocalDateTime createdAt;

  private Set<DeviceParameter> parameters = new HashSet<>();

  public Device(String name, DeviceState state) {
    this.name = name;
    this.state = state;
  }
}
