package com.kristijan.iotdesk.application.config;

import com.kristijan.iotdesk.domain.device.repositories.DevicesRepository;
import com.kristijan.iotdesk.domain.device.services.CreateDeviceService;
import com.kristijan.iotdesk.domain.device.services.ListDevicesService;
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
  public CreateDeviceService createDeviceService(DevicesRepository devicesRepository, Clock clock) {
    return new CreateDeviceService(devicesRepository, clock);
  }

}
