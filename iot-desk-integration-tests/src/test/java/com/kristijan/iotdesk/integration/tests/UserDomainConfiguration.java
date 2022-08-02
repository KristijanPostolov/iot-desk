package com.kristijan.iotdesk.integration.tests;

import com.kristijan.iotdesk.domain.user.repositories.UserRepository;
import com.kristijan.iotdesk.domain.user.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserDomainConfiguration {

  @Bean
  public UserService userService(UserRepository userRepository) {
    return new UserService(userRepository);
  }
}
