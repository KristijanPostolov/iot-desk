package com.kristijan.iotdesk.application.controllers;

import com.kristijan.iotdesk.application.dtos.ParameterSnapshotDto;
import com.kristijan.iotdesk.application.services.SnapshotsApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("api/v1/parameters")
@RequiredArgsConstructor
public class ParameterController {

  private final SnapshotsApplicationService snapshotsApplicationService;

  @GetMapping("/{parameterId}/snapshots")
  public List<ParameterSnapshotDto> getParameterSnapshots(@PathVariable long parameterId,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime beginRange,
                                                          @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endRange) {
    return snapshotsApplicationService.getParameterSnapshots(parameterId, beginRange, endRange);
  }
}
