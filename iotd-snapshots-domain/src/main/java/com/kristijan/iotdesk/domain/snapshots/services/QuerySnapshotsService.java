package com.kristijan.iotdesk.domain.snapshots.services;

import com.kristijan.iotdesk.domain.snapshots.models.ParameterSnapshot;
import com.kristijan.iotdesk.domain.snapshots.repositories.ParameterSnapshotRepository;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service that provides methods for querying parameter snapshots.
 */
@RequiredArgsConstructor
public class QuerySnapshotsService {

  private final ParameterSnapshotRepository parameterSnapshotRepository;

  public List<ParameterSnapshot> getSnapshotsInTimeRange(long parameterId, ZonedDateTime beginRange,
                                                         ZonedDateTime endRange) {
    List<ParameterSnapshot> snapshots =
      parameterSnapshotRepository.findByParameterIdAndTimeRangeOrderedAscending(parameterId, beginRange, endRange);
    return Optional.ofNullable(snapshots).orElse(Collections.emptyList());
  }

}
