package com.kristijan.iotdesk.domain.device.services;

import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.repositories.DevicesRepository;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class ListDevicesService {

  private final DevicesRepository devicesRepository;

  public List<Device> getAllDevices() {
    return Optional.ofNullable(devicesRepository.findAll())
      .orElse(Collections.emptyList());
  }

}
