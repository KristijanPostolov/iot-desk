package com.kristijan.iotdesk.integration.tests.cases;

import com.kristijan.iotdesk.domain.user.models.User;
import com.kristijan.iotdesk.domain.user.services.UserService;
import com.kristijan.iotdesk.integration.tests.IntegrationTestConfiguration;
import com.kristijan.iotdesk.persistence.mock.repositories.UserRepositoryMock;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = IntegrationTestConfiguration.class)
public class UserUseCaseIntegrationTest {

  @Autowired
  private UserService userService;

  @Autowired
  private UserRepositoryMock userRepositoryMock;

  @AfterEach
  void tearDown() {
    userRepositoryMock.reset();
  }

  @Test
  void shouldReturnEmptyWhenUserDoesNotExist() {
    userRepositoryMock.saveUser(new User("user1", "asdfg123"));

    Optional<User> result = userService.findByUsername("user2");

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnUserByUsername() {
    userRepositoryMock.saveUser(new User("user1", "asdfg123"));
    userRepositoryMock.saveUser(new User("user2", "password1"));
    userRepositoryMock.saveUser(new User("user3", "123123"));

    Optional<User> result = userService.findByUsername("user2");

    assertTrue(result.isPresent());
    assertEquals("user2", result.get().getUsername());
    assertEquals("password1", result.get().getPassword());
  }
}
