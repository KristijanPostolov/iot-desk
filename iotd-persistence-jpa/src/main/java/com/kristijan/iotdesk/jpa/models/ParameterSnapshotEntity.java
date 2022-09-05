package com.kristijan.iotdesk.jpa.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Table(name = "parameter_snapshot")
@Getter
@Setter
@NoArgsConstructor
public class ParameterSnapshotEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "parameter_id")
  private Long parameterId;

  @Column(name = "value_timestamp")
  private ZonedDateTime valueTimestamp;

  @Column(name = "value")
  private Double value;

  public ParameterSnapshotEntity(Long parameterId, ZonedDateTime valueTimestamp, Double value) {
    this.parameterId = parameterId;
    this.valueTimestamp = valueTimestamp;
    this.value = value;
  }
}
