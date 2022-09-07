package com.kristijan.iotdesk.domain.device.exceptions;

/**
 * Exception that occurred due to an error within a service on which the domain depends.
 * The exception originates outside of the domain.
 */
public class TransientDomainException extends RuntimeException {
  public TransientDomainException(String message) {
    super(message);
  }

  public TransientDomainException(String message, Throwable cause) {
    super(message, cause);
  }
}
