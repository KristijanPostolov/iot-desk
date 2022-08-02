package com.kristijan.iotdesk.persistence.mock.repositories;

import com.kristijan.iotdesk.domain.user.models.User;
import com.kristijan.iotdesk.domain.user.repositories.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class UserRepositoryMock implements UserRepository {

  private final Map<String, User> usersByUsername = new HashMap<>();

  @Override
  public Optional<User> findByUsername(String username) {
    return Optional.ofNullable(usersByUsername.get(username));
  }

  public void saveUser(User user) {
    usersByUsername.put(user.getUsername(), user);
  }

  public void reset() {
    usersByUsername.clear();
  }
}
