package com.kristijan.iotdesk.messaging.mqtt.exceptions;

import org.eclipse.paho.client.mqttv3.MqttException;

public class MqttRuntimeException extends RuntimeException {

  public MqttRuntimeException(String message, MqttException cause) {
    super(message, cause);
  }

}
