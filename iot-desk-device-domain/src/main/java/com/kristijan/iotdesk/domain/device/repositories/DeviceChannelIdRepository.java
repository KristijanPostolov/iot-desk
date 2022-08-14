package com.kristijan.iotdesk.domain.device.repositories;

import com.kristijan.iotdesk.domain.device.models.DeviceChannelId;

import java.util.Optional;

/**
 * Repository interface for accessing and modifying channel ids of devices.
 */
public interface DeviceChannelIdRepository {

  void save(DeviceChannelId deviceChannelId);

  Optional<DeviceChannelId> findByDeviceId(long deviceId);

  Optional<DeviceChannelId> findByChannelId(String channelId);
}
