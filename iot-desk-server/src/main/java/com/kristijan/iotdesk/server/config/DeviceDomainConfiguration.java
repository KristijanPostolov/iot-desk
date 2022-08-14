package com.kristijan.iotdesk.server.config;

import com.kristijan.iotdesk.domain.device.ports.ChannelIdGenerator;
import com.kristijan.iotdesk.domain.device.repositories.DeviceChannelIdRepository;
import com.kristijan.iotdesk.domain.device.repositories.DevicesRepository;
import com.kristijan.iotdesk.domain.device.services.ChannelIdService;
import com.kristijan.iotdesk.domain.device.services.ListDevicesService;
import com.kristijan.iotdesk.domain.device.services.ManageDevicesService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Spring java configuration for device domain module.
 */
@Configuration
public class DeviceDomainConfiguration {

  @Bean
  public ListDevicesService listDevicesService(DevicesRepository devicesRepository) {
    return new ListDevicesService(devicesRepository);
  }

  @Bean
  public ManageDevicesService manageDevicesService(DevicesRepository devicesRepository,
                                                   ChannelIdService channelIdService, Clock clock) {
    return new ManageDevicesService(devicesRepository, channelIdService, clock);
  }

  @Bean
  public ChannelIdService channelIdService(DeviceChannelIdRepository deviceChannelIdRepository,
                                           ChannelIdGenerator channelIdGenerator) {
    return new ChannelIdService(deviceChannelIdRepository, channelIdGenerator);
  }

}
