package com.kristijan.iotdesk.domain.device.services;

import com.kristijan.iotdesk.domain.device.exceptions.DomainException;
import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceState;
import com.kristijan.iotdesk.domain.device.repositories.DevicesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Service that provides methods for creating new devices.
 */
@RequiredArgsConstructor
@Slf4j
public class CreateDeviceService {

  private final DevicesRepository devicesRepository;
  private final ChannelIdService channelIdService;
  private final Clock clock;

  /**
   * Creates a new device with a given name.
   *
   * @param name name of the new device.
   * @return id of the newly created device.
   * @throws DomainException if the name is not valid.
   */
  public long createNewDevice(String name) {
    if (StringUtils.isBlank(name)) {
      throw new DomainException("Device name must not be null or blank");
    }
    Device newDevice = new Device(name, DeviceState.NEW);
    newDevice.setCreatedAt(LocalDateTime.now(clock));
    log.info("Creating new device with name: {}", name);
    long deviceId = devicesRepository.save(newDevice);
    channelIdService.generateNewDeviceChannelId(deviceId);
    return deviceId;
  }
}
