package com.kristijan.iotdesk.domain.snapshots.repositories;

import com.kristijan.iotdesk.domain.snapshots.models.ParameterSnapshot;

import java.time.ZonedDateTime;
import java.util.List;

/**
 * Repository for accessing and adding parameter snapshots.
 */
public interface ParameterSnapshotRepository {
  void saveParameterSnapshots(List<ParameterSnapshot> parameterSnapshots);

  List<ParameterSnapshot> findByParameterIdAndTimeRangeOrderedAscending(long parameterId, ZonedDateTime beginRange,
                                                                        ZonedDateTime endRange);
}
