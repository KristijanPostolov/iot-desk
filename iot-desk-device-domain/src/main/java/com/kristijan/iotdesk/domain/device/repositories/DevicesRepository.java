package com.kristijan.iotdesk.domain.device.repositories;

import com.kristijan.iotdesk.domain.device.models.Device;

import java.util.List;

public interface DevicesRepository {
  List<Device> findAll();

  long save(Device device);
}
