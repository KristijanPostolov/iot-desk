package com.kristijan.iotdesk.integration.tests;

import com.kristijan.iotdesk.persistence.mock.PersistenceMockComponents;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootConfiguration
@Import(DeviceDomainConfiguration.class)
@ComponentScan(basePackageClasses = PersistenceMockComponents.class)
public class IntegrationTestConfiguration {

}
