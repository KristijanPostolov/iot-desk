package com.kristijan.iotdesk.application.controllers;

import com.kristijan.iotdesk.application.exceptions.NotFoundException;
import com.kristijan.iotdesk.domain.device.exceptions.DomainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  void handleNotFoundException(NotFoundException ex) {
    log.warn(ex.getMessage());
  }

  @ExceptionHandler(DomainException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  void handleDomainException(DomainException ex) {
    log.warn(ex.getMessage(), ex);
  }
}
