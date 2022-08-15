package com.kristijan.iotdesk.persistence.mock.repositories;

import com.kristijan.iotdesk.domain.snapshots.models.ParameterSnapshot;
import com.kristijan.iotdesk.domain.snapshots.repositories.ParameterSnapshotRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ParameterSnapshotRepositoryMock implements ParameterSnapshotRepository {

  private static long PARAMETER_SNAPSHOTS_SEQUENCE = 1L;
  private final Map<Long, ParameterSnapshot> snapshots = new HashMap<>();

  @Override
  public void saveParameterSnapshots(List<ParameterSnapshot> parameterSnapshots) {
    parameterSnapshots.forEach(snapshot -> {
      long id = PARAMETER_SNAPSHOTS_SEQUENCE++;
      snapshot.setId(id);
      snapshots.put(id, snapshot);
    });
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
