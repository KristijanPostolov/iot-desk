package com.kristijan.iotdesk.persistence.mock.repositories;

import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.repositories.DevicesRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DevicesRepositoryMock implements DevicesRepository {

  private static long idSequence = 1L;
  private List<Device> devices = new ArrayList<>();

  @Override
  public List<Device> findAll() {
    return devices;
  }

  @Override
  public long save(Device device) {
    device.setId(idSequence++);
    devices.add(device);
    return device.getId();
  }

  void setDevices(List<Device> devices) {
    this.devices = devices;
  }

  public void reset() {
    setDevices(new ArrayList<>());
    idSequence = 1L;
  }
}
