package com.kristijan.iotdesk.application.controllers;

import com.kristijan.iotdesk.application.dtos.CreateDeviceDto;
import com.kristijan.iotdesk.application.dtos.DeviceDto;
import com.kristijan.iotdesk.application.services.DevicesApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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

  @PostMapping
  public ResponseEntity<Void> createDevice(@RequestBody CreateDeviceDto createDeviceDto) {
    long id = devicesApplicationService.createNewDevice(createDeviceDto);
    return ResponseEntity.created(URI.create("/api/v1/devices/" + id)).build();
  }
}
