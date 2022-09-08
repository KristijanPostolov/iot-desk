package com.kristijan.iotdesk.jpa.jparepositories;

import com.kristijan.iotdesk.jpa.models.DeviceCommandEntity;
import com.kristijan.iotdesk.jpa.models.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DeviceCommandRepositoryJpa extends JpaRepository<DeviceCommandEntity, Long> {

  List<DeviceCommandEntity> findByDeviceAndSentAtBetweenOrderBySentAtAsc(DeviceEntity deviceEntity,
                                                                         LocalDateTime beginRange,
                                                                         LocalDateTime endRange);

  Optional<DeviceCommandEntity> findByCommandId(String commandId);
}
