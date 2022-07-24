package com.kristijan.iotdesk.application.services;

import com.kristijan.iotdesk.application.dtos.CreateDeviceDto;
import com.kristijan.iotdesk.application.dtos.DeviceDto;
import com.kristijan.iotdesk.application.exceptions.NotFoundException;
import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.services.CreateDeviceService;
import com.kristijan.iotdesk.domain.device.services.ListDevicesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DevicesApplicationService {

  private final ListDevicesService listDevicesService;
  private final CreateDeviceService createDeviceService;

  public List<DeviceDto> getAllDevices() {
    return listDevicesService.getAllDevices().stream()
      .map(this::mapDeviceToDto)
      .collect(Collectors.toList());
  }

  public long createNewDevice(CreateDeviceDto createDeviceDto) {
    return createDeviceService.createNewDevice(createDeviceDto.getName());
  }

  public DeviceDto getDeviceById(long id) {
    return listDevicesService.findById(id)
      .map(this::mapDeviceToDto)
      .orElseThrow(() -> new NotFoundException("Device with the given id does not exist"));
  }

  private DeviceDto mapDeviceToDto(Device device) {
    return new DeviceDto(device.getId(), device.getName(), device.getState());
  }
}
