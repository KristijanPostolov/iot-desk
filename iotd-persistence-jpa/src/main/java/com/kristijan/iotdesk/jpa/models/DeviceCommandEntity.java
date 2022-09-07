package com.kristijan.iotdesk.jpa.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "device_command")
@Getter
@Setter
@NoArgsConstructor
public class DeviceCommandEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "command_id", updatable = false, unique = true)
  private String commandId;

  @Column(name = "content", updatable = false)
  private String content;

  @ManyToOne
  @JoinColumn(name = "device_id", updatable = false)
  private DeviceEntity device;

  @Column(name = "sent_at", updatable = false)
  private LocalDateTime sentAt;

  @Column(name = "acknowledged_at")
  private LocalDateTime acknowledgedAt;

  @Column(name = "ack_status")
  private short ackStatus;

  public DeviceCommandEntity(String commandId, String content, DeviceEntity device, LocalDateTime sentAt,
                             LocalDateTime acknowledgedAt, short ackStatus) {
    this.commandId = commandId;
    this.content = content;
    this.device = device;
    this.sentAt = sentAt;
    this.acknowledgedAt = acknowledgedAt;
    this.ackStatus = ackStatus;
  }
}
