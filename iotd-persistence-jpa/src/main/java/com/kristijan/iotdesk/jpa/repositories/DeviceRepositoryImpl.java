package com.kristijan.iotdesk.jpa.repositories;

import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceParameter;
import com.kristijan.iotdesk.domain.device.models.DeviceState;
import com.kristijan.iotdesk.domain.device.repositories.DevicesRepository;
import com.kristijan.iotdesk.jpa.jparepositories.DeviceRepositoryJpa;
import com.kristijan.iotdesk.jpa.models.DeviceEntity;
import com.kristijan.iotdesk.jpa.models.DeviceParameterEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeviceRepositoryImpl implements DevicesRepository {

  private final DeviceRepositoryJpa repository;

  @Override
  public List<Device> findAll() {
    return repository.findAll().stream()
      .map(this::mapDevice)
      .collect(Collectors.toList());
  }

  @Override
  public Optional<Device> findById(long id) {
    return repository.findById(id).map(this::mapDevice);
  }

  @Override
  public long save(Device device) {
    DeviceEntity entity = mapDevice(device);
    entity = repository.save(entity);
    return entity.getId();
  }

  @Override
  public Device updateStatus(Device device) {
    DeviceEntity entity = mapDevice(device);
    entity = repository.save(entity);
    return mapDevice(entity);
  }

  @Override
  public Device saveParameters(Device device, Set<DeviceParameter> parameters) {
    DeviceEntity entity = mapDevice(device);
    List<DeviceParameterEntity> parameterEntities = parameters.stream()
      .map(parameter -> mapDeviceParameter(parameter, entity))
      .collect(Collectors.toList());
    entity.getParameters().addAll(parameterEntities);
    return mapDevice(repository.save(entity));
  }

  private Device mapDevice(DeviceEntity entity) {
    Device device = new Device(entity.getName(), DeviceState.fromId(entity.getState()));
    device.setId(entity.getId());
    device.setCreatedAt(entity.getCreatedAt());

    List<DeviceParameter> parameters = entity.getParameters().stream()
      .map(this::mapDeviceParameter)
      .collect(Collectors.toList());
    device.getParameters().addAll(parameters);
    return device;
  }

  private DeviceEntity mapDevice(Device device) {
    DeviceEntity entity = new DeviceEntity(device.getName(), device.getState().getId(), device.getCreatedAt());
    entity.setId(device.getId());

    List<DeviceParameterEntity> parameters = device.getParameters().stream()
      .map(parameter -> mapDeviceParameter(parameter, entity))
      .collect(Collectors.toList());
    entity.setParameters(parameters);
    return entity;
  }

  private DeviceParameter mapDeviceParameter(DeviceParameterEntity parameterEntity) {
    DeviceParameter parameter = new DeviceParameter(parameterEntity.getDevice().getId(), parameterEntity.getAnchor(),
      parameterEntity.getName());
    parameter.setId(parameterEntity.getId());
    return parameter;
  }

  private DeviceParameterEntity mapDeviceParameter(DeviceParameter parameter, DeviceEntity deviceEntity) {
    DeviceParameterEntity entity = new DeviceParameterEntity(deviceEntity, parameter.getAnchor(),
      parameter.getName());
    entity.setId(parameter.getId());
    return entity;
  }
}
