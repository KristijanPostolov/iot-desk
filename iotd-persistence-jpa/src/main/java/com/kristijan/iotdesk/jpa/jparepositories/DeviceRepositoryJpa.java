package com.kristijan.iotdesk.jpa.jparepositories;

import com.kristijan.iotdesk.jpa.models.DeviceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepositoryJpa extends JpaRepository<DeviceEntity, Long> {

}
