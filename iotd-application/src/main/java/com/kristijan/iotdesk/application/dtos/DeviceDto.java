package com.kristijan.iotdesk.application.dtos;

import com.kristijan.iotdesk.domain.device.models.DeviceState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDto {
  private Long id;
  private String name;
  private DeviceState state;
}
