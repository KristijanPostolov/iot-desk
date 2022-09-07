package com.kristijan.iotdesk.jpa.repositories;

import com.kristijan.iotdesk.domain.snapshots.models.AcknowledgementStatus;
import com.kristijan.iotdesk.domain.snapshots.models.DeviceCommand;
import com.kristijan.iotdesk.domain.snapshots.repositories.DeviceCommandRepository;
import com.kristijan.iotdesk.jpa.jparepositories.DeviceCommandRepositoryJpa;
import com.kristijan.iotdesk.jpa.models.DeviceCommandEntity;
import com.kristijan.iotdesk.jpa.models.DeviceEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceCommandRepositoryImpl implements DeviceCommandRepository {

  private final EntityManager entityManager;
  private final DeviceCommandRepositoryJpa deviceCommandRepositoryJpa;

  @Override
  public void save(DeviceCommand deviceCommand) {
    DeviceCommandEntity entity = mapDeviceCommand(deviceCommand);
    deviceCommandRepositoryJpa.save(entity);
  }

  public List<DeviceCommand> findByDeviceIdAndSentAtTimeRangeOrderedAscending(Long deviceId, LocalDateTime beginRange,
                                                                              LocalDateTime endRange) {
    DeviceEntity deviceEntity = entityManager.getReference(DeviceEntity.class, deviceId);
    return deviceCommandRepositoryJpa.findByDeviceAndSentAtBetweenOrderBySentAtAsc(deviceEntity, beginRange, endRange)
      .stream()
      .map(this::mapDeviceCommand)
      .collect(Collectors.toList());
  }

  private DeviceCommandEntity mapDeviceCommand(DeviceCommand deviceCommand) {
    DeviceEntity deviceEntity = entityManager.getReference(DeviceEntity.class, deviceCommand.getDeviceId());
    DeviceCommandEntity entity = new DeviceCommandEntity(deviceCommand.getCommandId(), deviceCommand.getContent(),
      deviceEntity, deviceCommand.getSentAt(), deviceCommand.getAcknowledgedAt(), deviceCommand.getAckStatus().getId());
    entity.setId(deviceCommand.getId());
    return entity;
  }

  private DeviceCommand mapDeviceCommand(DeviceCommandEntity entity) {
    return new DeviceCommand(entity.getId(), entity.getCommandId(), entity.getContent(), entity.getDevice().getId(),
      entity.getSentAt(), entity.getAcknowledgedAt(), AcknowledgementStatus.fromId(entity.getAckStatus()));
  }
}
