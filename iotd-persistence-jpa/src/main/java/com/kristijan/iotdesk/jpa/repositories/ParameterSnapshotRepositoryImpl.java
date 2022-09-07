package com.kristijan.iotdesk.jpa.repositories;

import com.kristijan.iotdesk.domain.snapshots.models.ParameterSnapshot;
import com.kristijan.iotdesk.domain.snapshots.repositories.ParameterSnapshotRepository;
import com.kristijan.iotdesk.jpa.jparepositories.ParameterSnapshotRepositoryJpa;
import com.kristijan.iotdesk.jpa.models.ParameterSnapshotEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParameterSnapshotRepositoryImpl implements ParameterSnapshotRepository {

  private final ParameterSnapshotRepositoryJpa repository;

  @Override
  public void saveParameterSnapshots(List<ParameterSnapshot> parameterSnapshots) {
    List<ParameterSnapshotEntity> entities = parameterSnapshots.stream()
      .map(this::mapParameterSnapshot)
      .collect(Collectors.toList());
    repository.saveAll(entities);
  }

  @Override
  public List<ParameterSnapshot> findByParameterIdAndTimeRangeOrderedAscending(long parameterId,
                                                                               LocalDateTime beginRange,
                                                                               LocalDateTime endRange) {
    return repository.findByParameterIdAndValueTimestampBetweenOrderByValueTimestamp(parameterId, beginRange, endRange)
      .stream()
      .map(this::mapParameterSnapshot)
      .collect(Collectors.toList());
  }

  public List<ParameterSnapshot> findByParameterId(long parameterId) {
    return repository.findByParameterId(parameterId).stream()
      .map(this::mapParameterSnapshot)
      .collect(Collectors.toList());
  }

  private ParameterSnapshotEntity mapParameterSnapshot(ParameterSnapshot parameterSnapshot) {
    ParameterSnapshotEntity entity = new ParameterSnapshotEntity(parameterSnapshot.getParameterId(),
      parameterSnapshot.getTimestamp(), parameterSnapshot.getValue());
    entity.setId(parameterSnapshot.getId());
    return entity;
  }

  private ParameterSnapshot mapParameterSnapshot(ParameterSnapshotEntity entity) {
    ParameterSnapshot parameterSnapshot = new ParameterSnapshot(entity.getParameterId(),
      entity.getValueTimestamp(), entity.getValue());
    parameterSnapshot.setId(entity.getId());
    return parameterSnapshot;
  }
}
