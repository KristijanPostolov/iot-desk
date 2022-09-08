package com.kristijan.iotdesk.messaging.mqtt.services;

import com.kristijan.iotdesk.domain.snapshots.models.AnchorSnapshot;
import com.kristijan.iotdesk.messaging.mqtt.models.ParsingResult;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MqttSnapshotValidatorAndParser {
  private static final int MAX_LENGTH = 2000;
  private static final String PARAMETERS_SEPARATOR = ",";
  private static final String VALUE_SEPARATOR = ":";

  public ParsingResult<List<AnchorSnapshot>> parsePayload(byte[] payload) {
    if (!hasValidFormat(payload)) {
      return ParsingResult.invalid();
    }
    List<AnchorSnapshot> anchorSnapshots = parseMessage(payload);
    return ParsingResult.of(anchorSnapshots);
  }

  private List<AnchorSnapshot> parseMessage(byte[] payload) {
    String message = new String(payload);
    String[] parameters = message.split(PARAMETERS_SEPARATOR);

    return Arrays.stream(parameters)
      .map(this::parseAnchorSnapshot)
      .collect(Collectors.toList());
  }

  private AnchorSnapshot parseAnchorSnapshot(String parameter) {
    String[] parameterParts = parameter.split(VALUE_SEPARATOR);
    int anchor = Integer.parseInt(parameterParts[0]);
    double value = Double.parseDouble(parameterParts[1]);
    return new AnchorSnapshot(anchor, value);
  }

  private boolean hasValidFormat(byte[] payload) {
    String message = new String(payload);
    if (message.length() > MAX_LENGTH || message.isBlank() || message.endsWith(PARAMETERS_SEPARATOR)) {
      return false;
    }
    String[] parameters = message.split(PARAMETERS_SEPARATOR);
    Set<Integer> anchors = new HashSet<>();
    for (String parameter : parameters) {
      if (parameter.isBlank() || parameter.endsWith(VALUE_SEPARATOR)) {
        return false;
      }
      String[] parameterParts = parameter.split(VALUE_SEPARATOR);
      if (parameterParts.length != 2) {
        return false;
      }
      try {
        int anchor = Integer.parseInt(parameterParts[0]);
        Double.parseDouble(parameterParts[1]);
        anchors.add(anchor);
      } catch (NumberFormatException ex) {
        return false;
      }
    }
    return anchors.size() == parameters.length;
  }
}
