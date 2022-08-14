package com.kristijan.iotdesk.persistence.mock.repositories;

import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceParameter;
import com.kristijan.iotdesk.domain.device.repositories.DevicesRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class DevicesRepositoryMock implements DevicesRepository {

  private static long DEVICES_ID_SEQUENCE = 1L;
  private static long PARAMETERS_ID_SEQUENCE = 1L;
  private final Map<Long, Device> devices = new LinkedHashMap<>();

  @Override
  public List<Device> findAll() {
    return new ArrayList<>(devices.values());
  }

  @Override
  public long save(Device device) {
    long id = DEVICES_ID_SEQUENCE++;
    device.setId(id);
    devices.put(id, device);
    return id;
  }

  @Override
  public Optional<Device> findById(long id) {
    return Optional.ofNullable(devices.get(id));
  }

  @Override
  public Device updateStatus(Device device) {
    long id = device.getId();
    Device existing = devices.get(id);
    existing.setState(device.getState());
    return existing;
  }

  @Override
  public Device saveParameters(Device device, Set<DeviceParameter> parameters) {
    long id = device.getId();
    Device existing = devices.get(id);
    parameters.forEach(parameter -> {
      parameter.setId(PARAMETERS_ID_SEQUENCE++);
      existing.getParameters().add(parameter);
    });
    devices.put(id, existing);
    return existing;
  }

  public void reset() {
    this.devices.clear();
    DEVICES_ID_SEQUENCE = 1L;
    PARAMETERS_ID_SEQUENCE = 1L;
  }
}
