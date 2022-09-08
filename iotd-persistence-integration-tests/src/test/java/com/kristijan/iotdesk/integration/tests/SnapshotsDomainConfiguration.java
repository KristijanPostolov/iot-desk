package com.kristijan.iotdesk.integration.tests;

import com.kristijan.iotdesk.domain.device.services.ChannelIdService;
import com.kristijan.iotdesk.domain.device.services.ListDevicesService;
import com.kristijan.iotdesk.domain.device.services.ManageDevicesService;
import com.kristijan.iotdesk.domain.snapshots.ports.DeviceCommandSender;
import com.kristijan.iotdesk.domain.snapshots.repositories.DeviceCommandRepository;
import com.kristijan.iotdesk.domain.snapshots.repositories.ParameterSnapshotRepository;
import com.kristijan.iotdesk.domain.snapshots.services.AddDeviceSnapshotService;
import com.kristijan.iotdesk.domain.snapshots.services.DeviceCommandAckService;
import com.kristijan.iotdesk.domain.snapshots.services.DeviceCommandService;
import com.kristijan.iotdesk.domain.snapshots.services.DeviceMessagingErrorHandler;
import com.kristijan.iotdesk.domain.snapshots.services.QuerySnapshotsService;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

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
                                                           ParameterSnapshotRepository parameterSnapshotRepository) {
    return new AddDeviceSnapshotService(deviceMessagingErrorHandler, channelIdService, listDevicesService,
      manageDevicesService, parameterSnapshotRepository);
  }

  @Bean
  public QuerySnapshotsService querySnapshotsService(ParameterSnapshotRepository parameterSnapshotRepository) {
    return new QuerySnapshotsService(parameterSnapshotRepository);
  }

  @Bean
  public DeviceCommandService deviceCommandService(ListDevicesService listDevicesService,
                                                   ChannelIdService channelIdService,
                                                   DeviceCommandSender deviceCommandSender,
                                                   DeviceCommandRepository deviceCommandRepository, Clock clock) {
    return new DeviceCommandService(listDevicesService, channelIdService, deviceCommandSender, deviceCommandRepository,
      clock);
  }

  @Bean
  public DeviceCommandAckService deviceCommandAckService(ChannelIdService channelIdService,
                                                         DeviceMessagingErrorHandler deviceMessagingErrorHandler,
                                                         DeviceCommandRepository deviceCommandRepository,
                                                         Clock clock) {
    return new DeviceCommandAckService(channelIdService, deviceMessagingErrorHandler, deviceCommandRepository, clock);
  }

}
