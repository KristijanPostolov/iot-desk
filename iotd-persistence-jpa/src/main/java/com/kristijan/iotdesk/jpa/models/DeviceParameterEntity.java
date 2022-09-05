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

@Entity
@Table(name = "device_parameter")
@Getter
@Setter
@NoArgsConstructor
public class DeviceParameterEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "device_id", updatable = false)
  private DeviceEntity device;

  @Column(name = "anchor", updatable = false)
  private int anchor;

  @Column(name = "name")
  private String name;

  public DeviceParameterEntity(DeviceEntity device, int anchor, String name) {
    this.device = device;
    this.anchor = anchor;
    this.name = name;
  }
}
