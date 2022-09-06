package com.kristijan.iotdesk.application.services;

import com.kristijan.iotdesk.application.dtos.ParameterSnapshotDto;
import com.kristijan.iotdesk.domain.snapshots.models.ParameterSnapshot;
import com.kristijan.iotdesk.domain.snapshots.services.QuerySnapshotsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SnapshotsApplicationService {

  private final QuerySnapshotsService querySnapshotsService;
  private final Clock clock;

  public List<ParameterSnapshotDto> getParameterSnapshots(long parameterId, ZonedDateTime beginRange,
                                                          ZonedDateTime endRange) {
    return querySnapshotsService.getSnapshotsInTimeRange(parameterId, toLocal(beginRange), toLocal(endRange))
      .stream()
      .map(this::mapParameterSnapshotToDto)
      .collect(Collectors.toList());
  }

  private LocalDateTime toLocal(ZonedDateTime timestamp) {
    return LocalDateTime.ofInstant(timestamp.toInstant(), clock.getZone());
  }

  private ParameterSnapshotDto mapParameterSnapshotToDto(ParameterSnapshot parameterSnapshot) {
    ZonedDateTime timestamp = parameterSnapshot.getTimestamp().atZone(clock.getZone());
    return new ParameterSnapshotDto(timestamp, parameterSnapshot.getValue());
  }
}
