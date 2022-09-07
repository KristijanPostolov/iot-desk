package com.kristijan.iotdesk.domain.snapshots.repositories;

import com.kristijan.iotdesk.domain.snapshots.models.DeviceCommand;

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

}
