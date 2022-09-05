package com.kristijan.iotdesk.jpa.repositories;

import com.kristijan.iotdesk.domain.device.models.DeviceChannelId;
import com.kristijan.iotdesk.domain.device.repositories.DeviceChannelIdRepository;
import com.kristijan.iotdesk.jpa.jparepositories.DeviceChannelIdRepositoryJpa;
import com.kristijan.iotdesk.jpa.models.DeviceChannelIdEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeviceChannelIdRepositoryImpl implements DeviceChannelIdRepository {

  private final DeviceChannelIdRepositoryJpa repository;

  @Override
  public void save(DeviceChannelId deviceChannelId) {
    DeviceChannelIdEntity entity = mapDeviceChannelId(deviceChannelId);
    repository.save(entity);
  }

  @Override
  public Optional<DeviceChannelId> findByDeviceId(long deviceId) {
    return repository.findDeviceChannelIdEntityByDeviceId(deviceId).map(this::mapDeviceChannelId);
  }

  @Override
  public Optional<DeviceChannelId> findByChannelId(String channelId) {
    return repository.findDeviceChannelIdEntityByChannelId(channelId).map(this::mapDeviceChannelId);
  }

  private DeviceChannelIdEntity mapDeviceChannelId(DeviceChannelId deviceChannelId) {
    return new DeviceChannelIdEntity(null, deviceChannelId.getDeviceId(), deviceChannelId.getChannelId());
  }

  private DeviceChannelId mapDeviceChannelId(DeviceChannelIdEntity entity) {
    return new DeviceChannelId(entity.getDeviceId(), entity.getChannelId());
  }
}
