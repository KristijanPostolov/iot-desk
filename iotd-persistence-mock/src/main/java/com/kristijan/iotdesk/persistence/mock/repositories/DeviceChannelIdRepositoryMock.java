package com.kristijan.iotdesk.persistence.mock.repositories;

import com.kristijan.iotdesk.domain.device.models.DeviceChannelId;
import com.kristijan.iotdesk.domain.device.repositories.DeviceChannelIdRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class DeviceChannelIdRepositoryMock implements DeviceChannelIdRepository {

  private final Map<Long, DeviceChannelId> deviceChannelIdMap = new HashMap<>();

  @Override
  public void save(DeviceChannelId deviceChannelId) {
    deviceChannelIdMap.put(deviceChannelId.getDeviceId(), deviceChannelId);
  }

  @Override
  public Optional<DeviceChannelId> findByDeviceId(long deviceId) {
    return Optional.ofNullable(deviceChannelIdMap.get(deviceId));
  }

  @Override
  public Optional<DeviceChannelId> findByChannelId(String channelId) {
    return deviceChannelIdMap.values().stream()
      .filter(entry -> entry.getChannelId().equals(channelId))
      .findFirst();
  }

  public void reset() {
    deviceChannelIdMap.clear();
  }
}
