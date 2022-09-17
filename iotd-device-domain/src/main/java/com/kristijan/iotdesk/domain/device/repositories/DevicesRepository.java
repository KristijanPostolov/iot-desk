package com.kristijan.iotdesk.domain.device.repositories;

import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceParameter;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Repository interface that defines methods for accessing and modifying devices.
 */
public interface DevicesRepository {
  List<Device> findAll();

  long save(Device device);

  Optional<Device> findById(long id);

  Device updateStatus(Device device);

  Device saveParameters(Device device, Set<DeviceParameter> parameters);

  void updateParameters(Device device);
}
