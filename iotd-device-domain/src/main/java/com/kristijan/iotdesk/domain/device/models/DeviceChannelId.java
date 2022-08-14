package com.kristijan.iotdesk.domain.device.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Domain model for device's channel id.
 */
@Getter
@AllArgsConstructor
public class DeviceChannelId {

  private long deviceId;
  private String channelId;
}
