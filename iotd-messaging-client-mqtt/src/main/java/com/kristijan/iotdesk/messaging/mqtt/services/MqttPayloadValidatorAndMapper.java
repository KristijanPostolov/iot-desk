package com.kristijan.iotdesk.messaging.mqtt.services;

import com.kristijan.iotdesk.domain.snapshots.models.AnchorSnapshot;
import com.kristijan.iotdesk.messaging.mqtt.models.MappingResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MqttPayloadValidatorAndMapper {
  private static final int MAX_LENGTH = 2000;
  public static final String PARAMETERS_SEPARATOR = ",";
  public static final String VALUE_SEPARATOR = ":";

  public MappingResult mapPayload(byte[] payload) {
    if (!hasValidFormat(payload)) {
      return MappingResult.invalid();
    }

    List<AnchorSnapshot> anchorSnapshots = new ArrayList<>();
    String message = new String(payload);
    String[] parameters = message.split(PARAMETERS_SEPARATOR);
    for (String parameter : parameters) {
      String[] parameterParts = parameter.split(VALUE_SEPARATOR);
      int anchor = Integer.parseInt(parameterParts[0]);
      double value = Double.parseDouble(parameterParts[1]);
      anchorSnapshots.add(new AnchorSnapshot(anchor, value));
    }
    return MappingResult.of(anchorSnapshots);
  }

  private boolean hasValidFormat(byte[] payload) {
    String message = new String(payload);
    if (message.length() > MAX_LENGTH || message.isBlank() || message.endsWith(PARAMETERS_SEPARATOR)) {
      return false;
    }
    String[] parts = message.split(PARAMETERS_SEPARATOR);
    for (String parameter : parts) {
      if (parameter.isBlank() || parameter.endsWith(VALUE_SEPARATOR)) {
        return false;
      }
      String[] parameterParts = parameter.split(VALUE_SEPARATOR);
      if (parameterParts.length != 2) {
        return false;
      }
      try {
        Integer.parseInt(parameterParts[0]);
        Double.parseDouble(parameterParts[1]);
      } catch (NumberFormatException ex) {
        return false;
      }
    }
    return true;
  }
}
