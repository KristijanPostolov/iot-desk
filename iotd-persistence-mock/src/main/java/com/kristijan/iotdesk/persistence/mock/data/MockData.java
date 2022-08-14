package com.kristijan.iotdesk.persistence.mock.data;

import com.kristijan.iotdesk.domain.device.services.ManageDevicesService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 * Mock data for testing only.
 */
@Component
@Profile("dev")
@RequiredArgsConstructor
public class MockData {

  private final ManageDevicesService manageDevicesService;

  @PostConstruct
  private void insertMockData() {
    long deviceId1 = manageDevicesService.createNewDevice("Home manager");
    manageDevicesService.activateDevice(deviceId1);
    manageDevicesService.updateDeviceParameters(deviceId1, Set.of(1, 2, 3));

    long deviceId2 = manageDevicesService.createNewDevice("Arduino");
    manageDevicesService.activateDevice(deviceId2);
    manageDevicesService.updateDeviceParameters(deviceId2, Set.of(4, 5));

    long deviceId3 = manageDevicesService.createNewDevice("Raspberry Pi");
    manageDevicesService.activateDevice(deviceId3);
    manageDevicesService.updateDeviceParameters(deviceId3, Set.of(1, 2, 3, 7));
  }
}
