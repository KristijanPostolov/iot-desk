package com.kristijan.iotdesk.integration.tests;

import com.kristijan.iotdesk.domain.device.ports.ChannelIdGenerator;
import com.kristijan.iotdesk.domain.device.repositories.DeviceChannelIdRepository;
import com.kristijan.iotdesk.domain.device.repositories.DevicesRepository;
import com.kristijan.iotdesk.domain.device.services.ChannelIdService;
import com.kristijan.iotdesk.domain.device.services.CreateDeviceService;
import com.kristijan.iotdesk.domain.device.services.ListDevicesService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class DeviceDomainConfiguration {

  @Bean
  public ListDevicesService listDevicesService(DevicesRepository devicesRepository) {
    return new ListDevicesService(devicesRepository);
  }

  @Bean
  public CreateDeviceService createDeviceService(DevicesRepository devicesRepository, ChannelIdService channelIdService,
                                                 Clock clock) {
    return new CreateDeviceService(devicesRepository, channelIdService, clock);
  }

  @Bean
  public ChannelIdService channelIdService(DeviceChannelIdRepository deviceChannelIdRepository,
                                           ChannelIdGenerator channelIdGenerator) {
    return new ChannelIdService(deviceChannelIdRepository, channelIdGenerator);
  }

}