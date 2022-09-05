package com.kristijan.iotdesk.integration.tests.cases;

import com.kristijan.iotdesk.domain.user.models.User;
import com.kristijan.iotdesk.domain.user.services.UserService;
import com.kristijan.iotdesk.integration.tests.PostgresContainerTest;
import com.kristijan.iotdesk.jpa.repositories.UserRepositoryImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserUseCaseIntegrationTest extends PostgresContainerTest {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepositoryImpl userRepository;

  @Test
  void shouldReturnEmptyWhenUserDoesNotExist() {
    userRepository.save(new User("user1", "asdfg123"));

    Optional<User> result = userService.findByUsername("user2");

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnUserByUsername() {
    userRepository.save(new User("user1", "asdfg123"));
    userRepository.save(new User("user2", "password1"));
    userRepository.save(new User("user3", "123123"));

    Optional<User> result = userService.findByUsername("user2");

    assertTrue(result.isPresent());
    assertEquals("user2", result.get().getUsername());
    assertEquals("password1", result.get().getPassword());
  }
}
