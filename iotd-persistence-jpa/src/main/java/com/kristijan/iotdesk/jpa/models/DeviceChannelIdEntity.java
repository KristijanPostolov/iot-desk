package com.kristijan.iotdesk.jpa.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "device_channel_id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeviceChannelIdEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "device_id")
  private Long deviceId;

  @Column(name = "channel_id")
  private String channelId;

}
