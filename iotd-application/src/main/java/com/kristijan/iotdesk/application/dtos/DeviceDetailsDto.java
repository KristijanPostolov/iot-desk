package com.kristijan.iotdesk.application.dtos;

import com.kristijan.iotdesk.domain.device.models.DeviceState;
import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
public class DeviceDetailsDto extends DeviceDto {

  private ZonedDateTime createdAt;
  private List<DeviceParameterDto> parameters;

  public DeviceDetailsDto(Long id, String name, DeviceState state, ZonedDateTime createdAt,
                          List<DeviceParameterDto> parameters) {
    super(id, name, state);
    this.createdAt = createdAt;
    this.parameters = parameters;
  }
}
