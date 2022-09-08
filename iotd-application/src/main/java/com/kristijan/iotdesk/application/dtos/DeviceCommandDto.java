package com.kristijan.iotdesk.application.dtos;

import com.kristijan.iotdesk.domain.snapshots.models.AcknowledgementStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceCommandDto {
  private String commandId;
  private String content;
  private ZonedDateTime sentAt;
  private ZonedDateTime acknowledgedAt;
  private AcknowledgementStatus ackStatus;
}
