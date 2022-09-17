package com.kristijan.iotdesk.domain.device.services;

import com.kristijan.iotdesk.domain.device.exceptions.DomainException;
import com.kristijan.iotdesk.domain.device.models.Device;
import com.kristijan.iotdesk.domain.device.models.DeviceParameter;
import com.kristijan.iotdesk.domain.device.models.DeviceState;
import com.kristijan.iotdesk.domain.device.repositories.DevicesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service that provides methods for creating new devices.
 */
@RequiredArgsConstructor
@Slf4j
public class ManageDevicesService {

  public static final String DEFAULT_PARAMETER_NAME_PREFIX = "Parameter ";
  private final DevicesRepository devicesRepository;
  private final ChannelIdService channelIdService;
  private final Clock clock;

  /**
   * Creates a new device with a given name.
   *
   * @param name name of the new device.
   * @return id of the newly created device.
   * @throws DomainException if the name is not valid.
   */
  public long createNewDevice(String name) {
    if (StringUtils.isBlank(name)) {
      throw new DomainException("Device name must not be null or blank");
    }
    Device newDevice = new Device(name, DeviceState.NEW);
    newDevice.setCreatedAt(LocalDateTime.now(clock));
    log.info("Creating new device with name: {}", name);
    long deviceId = devicesRepository.save(newDevice);
    channelIdService.generateNewDeviceChannelId(deviceId);
    return deviceId;
  }

  /**
   * This method updates the status of a device to {@link DeviceState#ACTIVE}.
   *
   * @param id the id of the device to be activated.
   * @return the updated device.
   */
  public Device activateDevice(long id) {
    return devicesRepository.findById(id)
      .map(device -> {
        if (!DeviceState.NEW.equals(device.getState())) {
          throw new DomainException("Trying to activate device: " + id + ", but state is: " + device.getState());
        }
        device.setState(DeviceState.ACTIVE);
        log.info("Activating device with id: " + id);
        return devicesRepository.updateStatus(device);
      })
      .orElseThrow(() -> new DomainException("Trying to activate non existing device: " + id));
  }

  public Device updateDeviceParameters(long deviceId, Set<Integer> parameterAnchors) {
    return devicesRepository.findById(deviceId)
      .map(device -> {
        Set<DeviceParameter> deviceParameters = createNewParameterModels(device, parameterAnchors);
        if (!deviceParameters.isEmpty()) {
          logCreationOfParameters(deviceId, deviceParameters);
          return devicesRepository.saveParameters(device, deviceParameters);
        }
        return device;
      })
      .orElseThrow(() -> new DomainException("Creating parameters for non existing device: " + deviceId));
  }

  private void logCreationOfParameters(long deviceId, Set<DeviceParameter> deviceParameters) {
    List<Integer> anchors = deviceParameters.stream().map(DeviceParameter::getAnchor).collect(Collectors.toList());
    log.info("Creating new device parameters for device id: " + deviceId + ", anchors: " + anchors);
  }

  private Set<DeviceParameter> createNewParameterModels(Device device, Set<Integer> parameterAnchors) {
    Set<Integer> existingAnchors = device.getParameters().stream()
      .map(DeviceParameter::getAnchor)
      .collect(Collectors.toSet());

    return parameterAnchors.stream()
      .filter(anchor -> !existingAnchors.contains(anchor))
      .map(anchor -> mapToDeviceParameter(device.getId(), anchor))
      .collect(Collectors.toSet());
  }

  private DeviceParameter mapToDeviceParameter(long deviceId, int anchor) {
    return new DeviceParameter(deviceId, anchor, DEFAULT_PARAMETER_NAME_PREFIX + anchor);
  }

  /**
   * Renames the parameter defined by a device id and anchor.
   *
   * @param deviceId the device id for which we need to rename a parameter.
   * @param anchor anchor of the parameter that needs to be updated.
   * @param name the new name of the parameter.
   */
  public void renameDeviceParameter(long deviceId, int anchor, String name) {
    Optional<Device> deviceOptional = devicesRepository.findById(deviceId);
    Device device = deviceOptional
      .orElseThrow(() -> new DomainException("Renaming parameter for non existing device id: " + deviceId));

    DeviceParameter deviceParameter = device.getParameters().stream()
      .filter(parameter -> parameter.getAnchor() == anchor)
      .findFirst()
      .orElseThrow(() -> new DomainException("Non existing anchor: " + anchor + ", for device id: " + device));
    deviceParameter.setName(name);

    log.info("Renaming parameter for device id: " + deviceId + ", with anchor: " + anchor + ", to: " + name);
    devicesRepository.updateParameters(device);
  }

}
