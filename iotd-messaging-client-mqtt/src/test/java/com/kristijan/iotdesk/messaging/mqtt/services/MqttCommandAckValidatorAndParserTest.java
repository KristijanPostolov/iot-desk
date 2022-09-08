package com.kristijan.iotdesk.messaging.mqtt.services;

import com.kristijan.iotdesk.domain.snapshots.models.CommandAck;
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
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MqttCommandAckValidatorAndParserTest {

  @InjectMocks
  private MqttCommandAckValidatorAndParser mqttCommandAckValidatorAndParser;

  @ParameterizedTest
  @MethodSource("validationTestCases")
  void testValidation(String payload, boolean expectedValid) {
    ParsingResult<CommandAck> result =
      mqttCommandAckValidatorAndParser.parsePayload(payload.getBytes(StandardCharsets.UTF_8));

    assertEquals(expectedValid, result.isValid());
  }

  private static Stream<Arguments> validationTestCases() {
    return Stream.of(
      Arguments.of(StringUtils.repeat("a", 39), false), // too long
      Arguments.of(StringUtils.repeat("a", 37), false), // too short
      Arguments.of("", false), // empty
      Arguments.of(StringUtils.repeat("a", 38), false), // does not contain one ;
      Arguments.of(StringUtils.repeat("a", 35) + ";12", false), // more than 1 character after ;
      Arguments.of(UUID.randomUUID() + ";;", false), // contains multiple ;
      Arguments.of(UUID.randomUUID() + ";2", false), // status other that 0 or 1
      Arguments.of(UUID.randomUUID() + ";0", true),
      Arguments.of(UUID.randomUUID() + ";1", true)
    );
  }

  @Test
  void shouldReturnMappedAcsSuccessModel() {
    String commandId = UUID.randomUUID().toString();
    String message = commandId + ";1";

    ParsingResult<CommandAck> result =
      mqttCommandAckValidatorAndParser.parsePayload(message.getBytes(StandardCharsets.UTF_8));

    assertTrue(result.isValid());
    CommandAck ack = result.getResult();
    assertEquals(commandId, ack.getCommandId());
    assertTrue(ack.isSuccessful());
  }

  @Test
  void shouldReturnMappedAcsFailedModel() {
    String commandId = UUID.randomUUID().toString();
    String message = commandId + ";0";

    ParsingResult<CommandAck> result =
      mqttCommandAckValidatorAndParser.parsePayload(message.getBytes(StandardCharsets.UTF_8));

    assertTrue(result.isValid());
    CommandAck ack = result.getResult();
    assertEquals(commandId, ack.getCommandId());
    assertFalse(ack.isSuccessful());
  }
}