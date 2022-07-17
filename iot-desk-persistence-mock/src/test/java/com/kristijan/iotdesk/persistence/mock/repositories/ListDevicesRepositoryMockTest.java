package com.kristijan.iotdesk.persistence.mock.repositories;

import com.kristijan.iotdesk.domain.device.models.Device;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ListDevicesRepositoryMockTest {

  private ListDevicesRepositoryMock listDevicesRepositoryMock = new ListDevicesRepositoryMock();

  @Test
  void shouldReturnEmpty() {
    listDevicesRepositoryMock.setDevices(Collections.emptyList());
    List<Device> result = listDevicesRepositoryMock.findAll();

    assertNotNull(result);
    assertEquals(0, result.size());
  }

  @Test
  void shouldReturnMultipleDevices() {
    listDevicesRepositoryMock.setDevices(List.of(new Device("d1"), new Device("d2")));
    List<Device> result = listDevicesRepositoryMock.findAll();

    assertNotNull(result);
    assertEquals(2, result.size());
  }

}
