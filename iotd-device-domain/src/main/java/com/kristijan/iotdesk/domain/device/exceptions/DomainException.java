package com.kristijan.iotdesk.domain.device.exceptions;

/**
 * Exception within the domain.
 */
public class DomainException extends RuntimeException {

  public DomainException(String message) {
    super(message);
  }
}
