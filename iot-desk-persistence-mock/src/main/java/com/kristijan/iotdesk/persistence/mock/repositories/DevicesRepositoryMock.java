package com.kristijan.iotdesk.persistence.mock.repositories;

import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.repositories.DevicesRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class DevicesRepositoryMock implements DevicesRepository {

  private static long idSequence = 1L;
  private final Map<Long, Device> devices = new LinkedHashMap<>();

  @Override
  public List<Device> findAll() {
    return new ArrayList<>(devices.values());
  }

  @Override
  public long save(Device device) {
    long id = idSequence++;
    device.setId(id);
    devices.put(id, device);
    return id;
  }

  @Override
  public Optional<Device> findById(long id) {
    return Optional.ofNullable(devices.get(id));
  }

  public void reset() {
    this.devices.clear();
    idSequence = 1L;
  }
}
