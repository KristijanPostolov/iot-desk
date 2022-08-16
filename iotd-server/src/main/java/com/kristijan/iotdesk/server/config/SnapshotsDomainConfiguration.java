package com.kristijan.iotdesk.server.config;

import com.kristijan.iotdesk.domain.device.services.ChannelIdService;
import com.kristijan.iotdesk.domain.device.services.ListDevicesService;
import com.kristijan.iotdesk.domain.device.services.ManageDevicesService;
import com.kristijan.iotdesk.domain.snapshots.repositories.ParameterSnapshotRepository;
import com.kristijan.iotdesk.domain.snapshots.services.AddDeviceSnapshotService;
import com.kristijan.iotdesk.domain.snapshots.services.DeviceMessagingErrorHandler;
import com.kristijan.iotdesk.domain.snapshots.services.QuerySnapshotsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

/**
 * Spring java configuration for snapshots domain module.
 */
@Configuration
public class SnapshotsDomainConfiguration {

  @Bean
  public DeviceMessagingErrorHandler deviceMessagingErrorHandler() {
    return new DeviceMessagingErrorHandler();
  }

  @Bean
  public AddDeviceSnapshotService addDeviceSnapshotService(DeviceMessagingErrorHandler deviceMessagingErrorHandler,
                                                           ChannelIdService channelIdService,
                                                           ListDevicesService listDevicesService,
                                                           ManageDevicesService manageDevicesService,
                                                           ParameterSnapshotRepository parameterSnapshotRepository,
                                                           Clock clock) {
    return new AddDeviceSnapshotService(deviceMessagingErrorHandler, channelIdService, listDevicesService,
      manageDevicesService, parameterSnapshotRepository, clock);
  }

  @Bean
  public QuerySnapshotsService querySnapshotsService(ParameterSnapshotRepository parameterSnapshotRepository) {
    return new QuerySnapshotsService(parameterSnapshotRepository);
  }
}
