package com.kristijan.iotdesk.application.services;

import com.kristijan.iotdesk.application.dtos.ChannelIdDto;
import com.kristijan.iotdesk.application.dtos.CreateDeviceDto;
import com.kristijan.iotdesk.application.dtos.DeviceDetailsDto;
import com.kristijan.iotdesk.application.dtos.DeviceDto;
import com.kristijan.iotdesk.application.dtos.DeviceParameterDto;
import com.kristijan.iotdesk.application.exceptions.NotFoundException;
import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceParameter;
import com.kristijan.iotdesk.domain.device.services.ChannelIdService;
import com.kristijan.iotdesk.domain.device.services.ListDevicesService;
import com.kristijan.iotdesk.domain.device.services.ManageDevicesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DevicesApplicationService {

  private final ListDevicesService listDevicesService;
  private final ManageDevicesService manageDevicesService;
  private final ChannelIdService channelIdService;
  private final Clock clock;

  public List<DeviceDto> getAllDevices() {
    return listDevicesService.getAllDevices().stream()
      .map(device -> new DeviceDto(device.getId(), device.getName(), device.getState()))
      .collect(Collectors.toList());
  }

  public long createNewDevice(CreateDeviceDto createDeviceDto) {
    return manageDevicesService.createNewDevice(createDeviceDto.getName());
  }

  public DeviceDetailsDto getDeviceById(long id) {
    return listDevicesService.findById(id)
      .map(this::mapToDeviceDetailsDto)
      .orElseThrow(() -> new NotFoundException("Device with the given id does not exist"));
  }

  private DeviceDetailsDto mapToDeviceDetailsDto(Device device) {
    ZonedDateTime zonedCreatedAt = ZonedDateTime.of(device.getCreatedAt(), clock.getZone());
    List<DeviceParameterDto> parameters = device.getParameters().stream()
      .map(this::mapToDeviceParameterDto)
      .sorted(Comparator.comparing(DeviceParameterDto::getName))
      .collect(Collectors.toList());
    return new DeviceDetailsDto(device.getId(), device.getName(), device.getState(), zonedCreatedAt, parameters);
  }

  private DeviceParameterDto mapToDeviceParameterDto(DeviceParameter deviceParameter) {
    return new DeviceParameterDto(deviceParameter.getId(), deviceParameter.getAnchor(), deviceParameter.getName());
  }

  public ChannelIdDto getChannelIdForDevice(long id) {
    return channelIdService.findByDeviceId(id)
      .map(deviceChannelId -> new ChannelIdDto(deviceChannelId.getChannelId()))
      .orElseThrow(() -> new NotFoundException("Channel id was not found for the device id"));
  }
}
