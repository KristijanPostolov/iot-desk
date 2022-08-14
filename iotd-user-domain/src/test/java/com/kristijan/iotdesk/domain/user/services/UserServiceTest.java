package com.kristijan.iotdesk.domain.user.services;

import com.kristijan.iotdesk.domain.user.models.User;
import com.kristijan.iotdesk.domain.user.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Test
  void shouldReturnEmptyWhenUsernameDoesNotExist() {
    Optional<User> result = userService.findByUsername("user1");

    assertTrue(result.isEmpty());
  }

  @Test
  void shouldReturnUserByUsername() {
    when(userRepository.findByUsername("user1")).thenReturn(Optional.of(new User("user1", "pass1")));

    Optional<User> result = userService.findByUsername("user1");

    assertTrue(result.isPresent());
    assertEquals("user1", result.get().getUsername());
    assertEquals("pass1", result.get().getPassword());
  }
}
