package com.kristijan.iotdesk.messaging.mqtt.services;

import com.kristijan.iotdesk.domain.snapshots.models.AnchorSnapshot;
import com.kristijan.iotdesk.messaging.mqtt.models.ParsingResult;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MqttPayloadValidatorAndParserTest {

  @InjectMocks
  private MqttPayloadValidatorAndParser mqttPayloadValidatorAndParser;


  @ParameterizedTest
  @MethodSource("validationTestCases")
  void testValidation(String payload, boolean expectedValid) {
    ParsingResult result = mqttPayloadValidatorAndParser.parsePayload(payload.getBytes(StandardCharsets.UTF_8));

    assertEquals(expectedValid, result.isValid());
  }


  private static Stream<Arguments> validationTestCases() {
    return Stream.of(
      Arguments.of(StringUtils.repeat("a", 2001), false), // too long
      Arguments.of("", false), // empty
      Arguments.of("  ", false), // blank
      Arguments.of("asdasdasdasdas", false), // should contain at least one anchor:value pair
      Arguments.of(":2", false), // empty anchor
      Arguments.of("asd:2", false), // non numeric anchor
      Arguments.of("2.3:2", false), // floating point anchor
      Arguments.of("1:", false), // empty value
      Arguments.of("1:asd", false), // non numeric value
      Arguments.of("1:23:456", false), // more than one ':' within a parameter
      Arguments.of("1:23:", false), // trailing ':' within a parameter
      Arguments.of(",1:25", false), // empty parameter
      Arguments.of("1:25,", false), // trailing comma
      Arguments.of("1:25,1:45.5", false), // multiple values for same anchor
      Arguments.of("1:25,2:57.4,5:024.45", true) // valid
    );
  }

  @Test
  void shouldReturnInvalidMappingResultWhenValidationFails() {
    byte[] payload = "invalidPayload".getBytes(StandardCharsets.UTF_8);

    ParsingResult result = mqttPayloadValidatorAndParser.parsePayload(payload);

    assertFalse(result.isValid());
  }

  @Test
  void shouldReturnMappedModel() {
    byte[] payload = "3:4.56,1:99,4:123.456789".getBytes(StandardCharsets.UTF_8);

    ParsingResult result = mqttPayloadValidatorAndParser.parsePayload(payload);

    assertTrue(result.isValid());
    List<AnchorSnapshot> snapshots = result.getAnchorSnapshots();
    assertEquals(3, snapshots.size());
    assertParameterSnapshot(snapshots.get(0), 3, 4.56);
    assertParameterSnapshot(snapshots.get(1), 1, 99);
    assertParameterSnapshot(snapshots.get(2), 4, 123.456789);
  }

  private void assertParameterSnapshot(AnchorSnapshot snapshot, int expectedAnchor, double expectedValue) {
    assertEquals(expectedAnchor, snapshot.getAnchor());
    assertEquals(expectedValue, snapshot.getValue());
  }
}