package com.kristijan.iotdesk.application.services;

import com.kristijan.iotdesk.application.dtos.ChannelIdDto;
import com.kristijan.iotdesk.application.dtos.CreateDeviceDto;
import com.kristijan.iotdesk.application.dtos.DeviceDetailsDto;
import com.kristijan.iotdesk.application.dtos.DeviceDto;
import com.kristijan.iotdesk.application.exceptions.NotFoundException;
import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.services.ChannelIdService;
import com.kristijan.iotdesk.domain.device.services.CreateDeviceService;
import com.kristijan.iotdesk.domain.device.services.ListDevicesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DevicesApplicationService {

  private final ListDevicesService listDevicesService;
  private final CreateDeviceService createDeviceService;
  private final ChannelIdService channelIdService;
  private final Clock clock;

  public List<DeviceDto> getAllDevices() {
    return listDevicesService.getAllDevices().stream()
      .map(device -> new DeviceDto(device.getId(), device.getName(), device.getState()))
      .collect(Collectors.toList());
  }

  public long createNewDevice(CreateDeviceDto createDeviceDto) {
    return createDeviceService.createNewDevice(createDeviceDto.getName());
  }

  public DeviceDetailsDto getDeviceById(long id) {
    return listDevicesService.findById(id)
      .map(this::mapToDeviceDetailsDto)
      .orElseThrow(() -> new NotFoundException("Device with the given id does not exist"));
  }

  private DeviceDetailsDto mapToDeviceDetailsDto(Device device) {
    ZonedDateTime zonedCreatedAt = ZonedDateTime.of(device.getCreatedAt(), clock.getZone());
    return new DeviceDetailsDto(device.getId(), device.getName(), device.getState(), zonedCreatedAt);
  }

  public ChannelIdDto getChannelIdForDevice(long id) {
    return channelIdService.findByDeviceId(id)
      .map(deviceChannelId -> new ChannelIdDto(deviceChannelId.getChannelId()))
      .orElseThrow(() -> new NotFoundException("Channel id was not found for the device id"));
  }
}
