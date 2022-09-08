package com.kristijan.iotdesk.domain.snapshots.repositories;

import com.kristijan.iotdesk.domain.snapshots.models.DeviceCommand;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for adding and querying device commands.
 */
public interface DeviceCommandRepository {

  /**
   * Persists a device command.
   *
   * @param deviceCommand the device command.
   */
  void save(DeviceCommand deviceCommand);

  List<DeviceCommand> findByDeviceIdAndSentAtTimeRangeOrderedAscending(Long deviceId, LocalDateTime beginRange,
                                                                       LocalDateTime endRange);
}
