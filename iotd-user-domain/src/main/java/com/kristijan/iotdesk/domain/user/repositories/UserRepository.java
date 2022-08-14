package com.kristijan.iotdesk.domain.user.repositories;

import com.kristijan.iotdesk.domain.user.models.User;

import java.util.Optional;

/**
 * Repository interface that defines methods for accessing and modifying users.
 */
public interface UserRepository {
  Optional<User> findByUsername(String username);

}
