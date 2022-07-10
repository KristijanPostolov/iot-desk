package com.kristijan.iotdesk.domain.device.repositories;

import com.kristijan.iotdesk.domain.device.models.Device;

import java.util.List;

public interface ListDevicesRepository {
  List<Device> findAll();
}
