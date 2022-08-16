package com.kristijan.iotdesk.application.controllers;

import com.kristijan.iotdesk.application.dtos.ParameterSnapshotDto;
import com.kristijan.iotdesk.application.services.SnapshotsApplicationService;
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
class ParameterControllerTest {

  @InjectMocks
  private ParameterController parameterController;

  @Mock
  private SnapshotsApplicationService snapshotsApplicationService;

  @Test
  void shouldReturnListOfParameterSnapshots() {
    ZonedDateTime now = ZonedDateTime.now();
    List<ParameterSnapshotDto> listToReturn = List.of(new ParameterSnapshotDto(now, 1.2));
    when(snapshotsApplicationService.getParameterSnapshots(eq(1L), any(), any())).thenReturn(listToReturn);

    List<ParameterSnapshotDto> result =
      parameterController.getParameterSnapshots(1L, now, now.plusMinutes(1));

    assertEquals(listToReturn, result);
  }
}