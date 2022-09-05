package com.kristijan.iotdesk.jpa.config;

import com.kristijan.iotdesk.jpa.PersistenceJpaComponents;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan(basePackageClasses = PersistenceJpaComponents.class)
@EnableJpaRepositories(basePackageClasses = PersistenceJpaComponents.class)
public class PersistenceJpaConfiguration {

}
