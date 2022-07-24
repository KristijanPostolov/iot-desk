package com.kristijan.iotdesk.domain.device.services;

import com.kristijan.iotdesk.domain.device.exceptions.DomainException;
import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceState;
import com.kristijan.iotdesk.domain.device.repositories.DevicesRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.Clock;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class CreateDeviceService {

  private final DevicesRepository devicesRepository;
  private final Clock clock;

  public long createNewDevice(String name) {
    if (StringUtils.isBlank(name)) {
      throw new DomainException("Device name must not be null or blank");
    }
    Device newDevice = new Device(name, DeviceState.NEW);
    newDevice.setCreatedAt(LocalDateTime.now(clock));
    return devicesRepository.save(newDevice);
  }
}
