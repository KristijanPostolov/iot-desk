package com.kristijan.iotdesk.domain.snapshots.services;

import com.kristijan.iotdesk.domain.snapshots.models.ParameterSnapshot;
import com.kristijan.iotdesk.domain.snapshots.repositories.ParameterSnapshotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuerySnapshotsServiceTest {

  @InjectMocks
  private QuerySnapshotsService querySnapshotsService;

  @Mock
  private ParameterSnapshotRepository parameterSnapshotRepository;


  @Test
  void shouldReturnEmptyListWhenRepositoryReturnsNull() {
    ZonedDateTime from = ZonedDateTime.parse("2022-08-16T21:30:00Z");
    ZonedDateTime to = ZonedDateTime.parse("2022-08-16T23:30:00Z");
    when(parameterSnapshotRepository.findByParameterIdAndTimeRangeOrderedAscending(1L, from, to))
      .thenReturn(null);

    List<ParameterSnapshot> result = querySnapshotsService.getSnapshotsInTimeRange(1L, from, to);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }


  @Test
  void shouldReturnListOfSnapshots() {
    ZonedDateTime from = ZonedDateTime.parse("2022-08-16T21:30:00Z");
    ZonedDateTime to = ZonedDateTime.parse("2022-08-16T23:30:00Z");
    ParameterSnapshot snapshot1 = new ParameterSnapshot(1L, from.plusMinutes(1), 1.23);
    ParameterSnapshot snapshot2 = new ParameterSnapshot(1L, from.plusMinutes(3), 3.45);
    ParameterSnapshot snapshot3 = new ParameterSnapshot(1L, from.plusMinutes(10), 2.6);
    when(parameterSnapshotRepository.findByParameterIdAndTimeRangeOrderedAscending(1L, from, to))
      .thenReturn(List.of(snapshot1, snapshot2, snapshot3));

    List<ParameterSnapshot> result = querySnapshotsService.getSnapshotsInTimeRange(1L, from, to);

    assertEquals(3, result.size());
    assertEqualsSnapshot(snapshot1, result.get(0));
    assertEqualsSnapshot(snapshot2, result.get(1));
    assertEqualsSnapshot(snapshot3, result.get(2));
  }

  private void assertEqualsSnapshot(ParameterSnapshot expected, ParameterSnapshot actual) {
    assertEquals(expected.getTimestamp(), actual.getTimestamp());
    assertEquals(expected.getValue(), actual.getValue());
  }
}