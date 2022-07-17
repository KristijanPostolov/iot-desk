package com.kristijan.iotdesk.server;

import com.kristijan.iotdesk.application.ApplicationComponents;
import com.kristijan.iotdesk.persistence.mock.PersistenceMockComponents;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {ApplicationComponents.class, PersistenceMockComponents.class})
public class IotServerApplication {

  public static void main(String[] args) {
    SpringApplication.run(IotServerApplication.class, args);
  }
}
