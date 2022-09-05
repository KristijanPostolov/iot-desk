package com.kristijan.iotdesk.jpa.jparepositories;

import com.kristijan.iotdesk.jpa.models.ParameterSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface ParameterSnapshotRepositoryJpa extends JpaRepository<ParameterSnapshotEntity, Long> {

  List<ParameterSnapshotEntity> findByParameterId(Long parameterId);

  List<ParameterSnapshotEntity> findByParameterIdAndValueTimestampBetweenOrderByValueTimestamp(
    Long parameterId, ZonedDateTime beginRange, ZonedDateTime endRange);
}
