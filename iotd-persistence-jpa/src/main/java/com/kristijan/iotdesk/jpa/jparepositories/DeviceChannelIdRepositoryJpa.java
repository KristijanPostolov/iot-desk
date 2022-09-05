package com.kristijan.iotdesk.jpa.jparepositories;

import com.kristijan.iotdesk.jpa.models.DeviceChannelIdEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeviceChannelIdRepositoryJpa extends JpaRepository<DeviceChannelIdEntity, Long> {

  Optional<DeviceChannelIdEntity> findDeviceChannelIdEntityByDeviceId(Long deviceId);

  Optional<DeviceChannelIdEntity> findDeviceChannelIdEntityByChannelId(String deviceId);
}
