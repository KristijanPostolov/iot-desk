package com.kristijan.iotdesk.application.config;

import com.kristijan.iotdesk.domain.device.services.ListDevicesService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring java configuration for device domain module.
 */
@Configuration
public class DeviceDomainConfiguration {

  @Bean
  public ListDevicesService listDevicesService() {
    return new ListDevicesService();
  }

}
