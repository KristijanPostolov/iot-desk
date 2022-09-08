package com.kristijan.iotdesk.application.controllers;

import com.kristijan.iotdesk.application.dtos.CreateCommandDto;
import com.kristijan.iotdesk.application.dtos.DeviceCommandDto;
import com.kristijan.iotdesk.application.services.CommandsApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/commands")
@RequiredArgsConstructor
public class CommandController {

  private final CommandsApplicationService commandsApplicationService;

  @PostMapping
  public void postCommand(@RequestBody CreateCommandDto createCommandDto) {
    commandsApplicationService.postCommand(createCommandDto);
  }

  @GetMapping
  public List<DeviceCommandDto> getDeviceCommands(@RequestParam long deviceId,
                                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime beginRange,
                                                  @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endRange) {
    return commandsApplicationService.getDeviceCommands(deviceId, beginRange, endRange);
  }
}
