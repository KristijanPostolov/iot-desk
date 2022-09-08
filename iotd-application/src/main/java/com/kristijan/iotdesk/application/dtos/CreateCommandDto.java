package com.kristijan.iotdesk.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateCommandDto {
  private Long deviceId;
  private String commandContent;
}
