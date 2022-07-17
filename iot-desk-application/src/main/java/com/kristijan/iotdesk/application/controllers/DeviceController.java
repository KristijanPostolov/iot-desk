package com.kristijan.iotdesk.application.controllers;

import com.kristijan.iotdesk.application.dtos.DeviceDto;
import com.kristijan.iotdesk.application.services.DevicesApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/devices")
@RequiredArgsConstructor
public class DeviceController {

  private final DevicesApplicationService devicesApplicationService;

  @GetMapping
  public List<DeviceDto> getAllDevices() {
    return devicesApplicationService.getAllDevices();
  }
}
