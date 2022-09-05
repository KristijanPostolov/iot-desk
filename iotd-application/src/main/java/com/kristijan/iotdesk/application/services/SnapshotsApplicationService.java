package com.kristijan.iotdesk.application.services;

import com.kristijan.iotdesk.application.dtos.ParameterSnapshotDto;
import com.kristijan.iotdesk.domain.snapshots.models.ParameterSnapshot;
import com.kristijan.iotdesk.domain.snapshots.services.QuerySnapshotsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SnapshotsApplicationService {

  private final QuerySnapshotsService querySnapshotsService;

  public List<ParameterSnapshotDto> getParameterSnapshots(long parameterId, ZonedDateTime beginRange,
                                                          ZonedDateTime endRange) {
    return querySnapshotsService.getSnapshotsInTimeRange(parameterId, beginRange, endRange).stream()
      .map(this::mapParameterSnapshotToDto)
      .collect(Collectors.toList());
  }

  private ParameterSnapshotDto mapParameterSnapshotToDto(ParameterSnapshot parameterSnapshot) {
    return new ParameterSnapshotDto(parameterSnapshot.getTimestamp(), parameterSnapshot.getValue());
  }
}
