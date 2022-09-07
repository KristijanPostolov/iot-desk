package com.kristijan.iotdesk.domain.snapshots.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class DeviceCommand {

  @Setter
  private Long id;

  private String commandId;
  private String content;
  private Long deviceId;
  private LocalDateTime sentAt;
  @Setter
  private LocalDateTime acknowledgedAt;
  @Setter
  private AcknowledgementStatus ackStatus;

  public DeviceCommand(String commandId, String content, Long deviceId, LocalDateTime sentAt,
                       AcknowledgementStatus ackStatus) {
    this.commandId = commandId;
    this.content = content;
    this.deviceId = deviceId;
    this.sentAt = sentAt;
    this.ackStatus = ackStatus;
  }
}
