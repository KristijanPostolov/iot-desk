package com.kristijan.iotdesk.persistence.mock.repositories;

import com.kristijan.iotdesk.domain.snapshots.models.ParameterSnapshot;
import com.kristijan.iotdesk.domain.snapshots.repositories.ParameterSnapshotRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ParameterSnapshotRepositoryMock implements ParameterSnapshotRepository {

  private static long PARAMETER_SNAPSHOTS_SEQUENCE = 1L;
  private final Map<Long, ParameterSnapshot> snapshots = new LinkedHashMap<>();

  @Override
  public void saveParameterSnapshots(List<ParameterSnapshot> parameterSnapshots) {
    parameterSnapshots.forEach(snapshot -> {
      long id = PARAMETER_SNAPSHOTS_SEQUENCE++;
      snapshot.setId(id);
      snapshots.put(id, snapshot);
    });
  }

  @Override
  public List<ParameterSnapshot> findByParameterIdAndTimeRangeOrderedAscending(long parameterId,
                                                                               ZonedDateTime beginRange,
                                                                               ZonedDateTime endRange) {
    return snapshots.values().stream()
      .filter(snapshot -> parameterId == snapshot.getParameterId())
      .filter(snapshot -> snapshot.getTimestamp().isAfter(beginRange))
      .filter(snapshot -> snapshot.getTimestamp().isBefore(endRange))
      .sorted(Comparator.comparing(ParameterSnapshot::getTimestamp))
      .collect(Collectors.toList());
  }

  public void reset() {
    snapshots.clear();
  }

  public List<ParameterSnapshot> getByParameterId(long parameterId) {
    return snapshots.values().stream()
      .filter(snapshot -> snapshot.getParameterId() == parameterId)
      .collect(Collectors.toList());
  }
}
