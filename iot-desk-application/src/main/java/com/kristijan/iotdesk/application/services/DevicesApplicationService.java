package com.kristijan.iotdesk.application.services;

import com.kristijan.iotdesk.application.dtos.DeviceDto;
import com.kristijan.iotdesk.domain.device.services.ListDevicesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DevicesApplicationService {

  private final ListDevicesService listDevicesService;

  public List<DeviceDto> getAllDevices() {
    return listDevicesService.getAllDevices().stream()
      .map(device -> new DeviceDto(device.getName()))
      .collect(Collectors.toList());
  }
}
