package com.kristijan.iotdesk.application.services;

import com.kristijan.iotdesk.application.dtos.ParameterSnapshotDto;
import com.kristijan.iotdesk.domain.snapshots.models.ParameterSnapshot;
import com.kristijan.iotdesk.domain.snapshots.services.QuerySnapshotsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SnapshotsApplicationServiceTest {

  private SnapshotsApplicationService snapshotsApplicationService;

  @Mock
  private QuerySnapshotsService querySnapshotsService;

  private final LocalDateTime now = LocalDateTime.parse("2022-08-16T21:00:00");
  private final ZoneOffset offset = ZoneOffset.UTC;
  private final Clock clock = Clock.fixed(now.toInstant(offset), offset);

  @BeforeEach
  void setUp() {
    snapshotsApplicationService = new SnapshotsApplicationService(querySnapshotsService, clock);
  }

  @Test
  void shouldMapParameterSnapshotsToDtos() {
    ZonedDateTime nowZoned = ZonedDateTime.ofInstant(now, offset, ZoneOffset.of("+02:00"));
    ParameterSnapshot snapshot1 = new ParameterSnapshot(1, now.plusMinutes(1), 1.2);
    ParameterSnapshot snapshot2 = new ParameterSnapshot(1, now.plusMinutes(2), 3.0);
    when(querySnapshotsService.getSnapshotsInTimeRange(eq(1L), eq(now), eq(now.plusMinutes(10))))
      .thenReturn(List.of(snapshot1, snapshot2));

    List<ParameterSnapshotDto> result =
      snapshotsApplicationService.getParameterSnapshots(1L, nowZoned, nowZoned.plusMinutes(10));

    assertEquals(2, result.size());
    assertParameterSnapshotDto(result.get(0), now.plusMinutes(1).atZone(offset), 1.2);
    assertParameterSnapshotDto(result.get(1), now.plusMinutes(2).atZone(offset), 3.0);
  }

  private void assertParameterSnapshotDto(ParameterSnapshotDto actual, ZonedDateTime expectedTimestamp,
                                          Double expectedValue) {
    assertEquals(expectedTimestamp, actual.getTimestamp());
    assertEquals(expectedValue, actual.getValue());
  }
}