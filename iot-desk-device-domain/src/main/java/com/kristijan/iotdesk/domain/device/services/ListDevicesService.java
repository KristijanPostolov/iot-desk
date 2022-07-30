package com.kristijan.iotdesk.domain.device.services;

import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.repositories.DevicesRepository;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service which provides methods to access devices.
 */
@RequiredArgsConstructor
public class ListDevicesService {

  private final DevicesRepository devicesRepository;

  /**
   * Gets a list of all existing devices, or an empty list if none exists.
   *
   * @return list of all devices.
   */
  public List<Device> getAllDevices() {
    return Optional.ofNullable(devicesRepository.findAll())
      .orElse(Collections.emptyList());
  }

  /**
   * Finds device by id. Returns empty if device with given id does not exist.
   *
   * @param id id of the device.
   * @return Optional device, empty if it does not exist.
   */
  public Optional<Device> findById(long id) {
    return devicesRepository.findById(id);
  }
}
