package com.kristijan.iotdesk.application.dtos;

import com.kristijan.iotdesk.domain.device.models.DeviceState;
import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class DeviceDetailsDto extends DeviceDto {

  private ZonedDateTime createdAt;

  public DeviceDetailsDto(Long id, String name, DeviceState state, ZonedDateTime createdAt) {
    super(id, name, state);
    this.createdAt = createdAt;
  }
}
