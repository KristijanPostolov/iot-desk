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
        return devicesRepository.updateStatus(device);
      })
      .orElseThrow(() -> new DomainException("Trying to activate non existing device: " + id));
  }

  public Device updateDeviceParameters(long deviceId, Set<Integer> parameterAnchors) {
    return devicesRepository.findById(deviceId)
      .map(device -> {
        Set<DeviceParameter> deviceParameters = createNewParameterModels(device, parameterAnchors);
        if (!deviceParameters.isEmpty()) {
          return devicesRepository.saveParameters(device, deviceParameters);
        }
        return device;
      })
      .orElseThrow(() -> new DomainException("Creating parameters for non existing device: " + deviceId));
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

}
