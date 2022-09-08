package com.kristijan.iotdesk.messaging.mqtt.services;

import com.kristijan.iotdesk.domain.snapshots.models.CommandAck;
import com.kristijan.iotdesk.messaging.mqtt.models.ParsingResult;
import org.springframework.stereotype.Service;

@Service
public class MqttCommandAckValidatorAndParser {

  private static final int MESSAGE_LENGTH = 38;
  private static final String COMMAND_ID_SEPARATOR = ";";
  private static final String SUCCESS_ACS = "1";
  private static final String FAILURE_ACK = "0";

  public ParsingResult<CommandAck> parsePayload(byte[] payload) {
    String message = new String(payload);
    if (!hasValidFormat(message)) {
      return ParsingResult.invalid();
    }
    CommandAck commandAck = parseMessage(message);
    return ParsingResult.of(commandAck);
  }

  private CommandAck parseMessage(String message) {
    String[] parts = message.split(COMMAND_ID_SEPARATOR);
    return new CommandAck(parts[0], parts[1].equals(SUCCESS_ACS));
  }

  private boolean hasValidFormat(String message) {
    if (message.length() != MESSAGE_LENGTH) {
      return false;
    }
    String[] parts = message.split(COMMAND_ID_SEPARATOR);
    if (parts.length != 2) {
      return false;
    }
    String status = parts[1];
    return status.equals(FAILURE_ACK) || status.equals(SUCCESS_ACS);
  }
}
