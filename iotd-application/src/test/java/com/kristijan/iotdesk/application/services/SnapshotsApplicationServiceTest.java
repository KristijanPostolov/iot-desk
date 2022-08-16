package com.kristijan.iotdesk.application.services;

import com.kristijan.iotdesk.application.dtos.ParameterSnapshotDto;
import com.kristijan.iotdesk.domain.snapshots.models.ParameterSnapshot;
import com.kristijan.iotdesk.domain.snapshots.services.QuerySnapshotsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SnapshotsApplicationServiceTest {

  @InjectMocks
  private SnapshotsApplicationService snapshotsApplicationService;

  @Mock
  private QuerySnapshotsService querySnapshotsService;

  @Test
  void shouldMapParameterSnapshotsToDtos() {
    ZonedDateTime now = ZonedDateTime.parse("2022-08-16T21:00:00Z");
    ParameterSnapshot snapshot1 = new ParameterSnapshot(1, now.plusMinutes(1), 1.2);
    ParameterSnapshot snapshot2 = new ParameterSnapshot(1, now.plusMinutes(2), 3.0);
    when(querySnapshotsService.getSnapshotsInTimeRange(eq(1L), any(), any()))
      .thenReturn(List.of(snapshot1, snapshot2));

    List<ParameterSnapshotDto> result =
      snapshotsApplicationService.getParameterSnapshots(1L, now, now.plusMinutes(10));

    assertEquals(2, result.size());
    assertParameterSnapshotDto(snapshot1, result.get(0));
    assertParameterSnapshotDto(snapshot2, result.get(1));
  }

  private void assertParameterSnapshotDto(ParameterSnapshot expected, ParameterSnapshotDto actual) {
    assertEquals(expected.getTimestamp(), actual.getTimestamp());
    assertEquals(expected.getValue(), actual.getValue());
  }
}