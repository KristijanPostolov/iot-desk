package com.kristijan.iotdesk.jpa.jparepositories;

import com.kristijan.iotdesk.jpa.models.DeviceCommandEntity;
import com.kristijan.iotdesk.jpa.models.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DeviceCommandRepositoryJpa extends JpaRepository<DeviceCommandEntity, Long> {

  List<DeviceCommandEntity> findByDeviceAndSentAtBetweenOrderBySentAtAsc(DeviceEntity deviceEntity,
                                                                         LocalDateTime beginRange,
                                                                         LocalDateTime endRange);
}
