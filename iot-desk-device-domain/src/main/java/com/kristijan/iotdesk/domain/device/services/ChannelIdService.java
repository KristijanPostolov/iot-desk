package com.kristijan.iotdesk.domain.device.services;

import com.kristijan.iotdesk.domain.device.exceptions.TransientDomainException;
import com.kristijan.iotdesk.domain.device.models.DeviceChannelId;
import com.kristijan.iotdesk.domain.device.ports.ChannelIdGenerator;
import com.kristijan.iotdesk.domain.device.repositories.DeviceChannelIdRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Optional;

/**
 * Service which provides methods to access and manage channel ids.
 */
@RequiredArgsConstructor
@Slf4j
public class ChannelIdService {

  private final DeviceChannelIdRepository deviceChannelIdRepository;
  private final ChannelIdGenerator channelIdGenerator;

  public void generateNewDeviceChannelId(long deviceId) {
    String channelId = channelIdGenerator.generate();
    if (StringUtils.isBlank(channelId)) {
      throw new TransientDomainException("The generated channel id must not be null or blank");
    }
    log.info("Saving channel id for device: {}", deviceId);
    deviceChannelIdRepository.save(new DeviceChannelId(deviceId, channelId));
  }

  public Optional<DeviceChannelId> findByDeviceId(long deviceId) {
    return deviceChannelIdRepository.findByDeviceId(deviceId);
  }

}
