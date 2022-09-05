package com.kristijan.iotdesk.messaging.mqtt.services;

import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Interface that defines a message handler for mqtt messages. All implementations of this interface will be subscribed
 * to the mqtt client.
 */
public interface MqttMessageHandler {

  /**
   * This method provides the topicFilter for which the handler implementation will be subscribed to.
   *
   * @return topic filter for which the handles should be subscribed to.
   */
  String getTopicFilter();

  /**
   * This method handles mqtt messages that match the topicFilter returned by
   * {@link MqttMessageHandler#getTopicFilter()}.
   *
   * @param topic the topic for which the message was received.
   * @param message the mqtt message.
   */
  void handleMqttMessage(String topic, MqttMessage message);
}
