package com.kristijan.iotdesk.domain.user.services;

import com.kristijan.iotdesk.domain.user.models.User;
import com.kristijan.iotdesk.domain.user.repositories.UserRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

/**
 * Service that provides methods for accessing and managing users.
 */
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public Optional<User> findByUsername(String username) {
    return userRepository.findByUsername(username);
  }
}
