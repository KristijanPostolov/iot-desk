package com.kristijan.iotdesk.messaging.mqtt.models;

import com.kristijan.iotdesk.domain.snapshots.models.AnchorSnapshot;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MappingResultTest {

  @Test
  void shouldReturnInvalid() {
    MappingResult result = MappingResult.invalid();

    assertFalse(result.isValid());
    assertNull(result.getAnchorSnapshots());
  }

  @Test
  void shouldReturnValid() {
    List<AnchorSnapshot> list = List.of(new AnchorSnapshot(1, 2));

    MappingResult result = MappingResult.of(list);

    assertTrue(result.isValid());
    assertEquals(list, result.getAnchorSnapshots());
  }
}