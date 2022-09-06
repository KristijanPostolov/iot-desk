package com.kristijan.iotdesk.application.controllers;

import com.kristijan.iotdesk.application.dtos.ChannelIdDto;
import com.kristijan.iotdesk.application.dtos.CreateDeviceDto;
import com.kristijan.iotdesk.application.dtos.CreateDeviceResponseDto;
import com.kristijan.iotdesk.application.dtos.DeviceDetailsDto;
import com.kristijan.iotdesk.application.dtos.DeviceDto;
import com.kristijan.iotdesk.application.services.DevicesApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
  public ResponseEntity<CreateDeviceResponseDto> createDevice(@RequestBody CreateDeviceDto createDeviceDto) {
    CreateDeviceResponseDto responseDto = devicesApplicationService.createNewDevice(createDeviceDto);
    return ResponseEntity.created(URI.create("/api/v1/devices/" + responseDto.getId()))
      .body(responseDto);
  }

  @GetMapping("/{id}")
  public DeviceDetailsDto getDeviceById(@PathVariable Long id) {
    return devicesApplicationService.getDeviceById(id);
  }

  @GetMapping("/{id}/channelId")
  public ChannelIdDto getChannelIdForDevice(@PathVariable Long id) {
    return devicesApplicationService.getChannelIdForDevice(id);
  }
}
