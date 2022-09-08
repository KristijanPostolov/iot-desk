package com.kristijan.iotdesk.domain.snapshots.repositories;

import com.kristijan.iotdesk.domain.snapshots.models.DeviceCommand;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

  Optional<DeviceCommand> findByCommandId(String commandId);
}
