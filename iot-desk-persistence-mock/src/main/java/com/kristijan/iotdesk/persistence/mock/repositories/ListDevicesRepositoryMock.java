package com.kristijan.iotdesk.persistence.mock.repositories;

import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.repositories.ListDevicesRepository;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
public class ListDevicesRepositoryMock implements ListDevicesRepository {

  private List<Device> devices = Collections.emptyList();

  @Override
  public List<Device> findAll() {
    return devices;
  }

  void setDevices(List<Device> devices) {
    this.devices = devices;
  }
}
