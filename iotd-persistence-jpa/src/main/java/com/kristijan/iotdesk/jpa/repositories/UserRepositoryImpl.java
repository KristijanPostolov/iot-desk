package com.kristijan.iotdesk.jpa.repositories;

import com.kristijan.iotdesk.domain.user.models.User;
import com.kristijan.iotdesk.domain.user.repositories.UserRepository;
import com.kristijan.iotdesk.jpa.jparepositories.UserRepositoryJpa;
import com.kristijan.iotdesk.jpa.models.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

  private final UserRepositoryJpa repository;

  @Override
  public Optional<User> findByUsername(String username) {
    return repository.findByUsername(username).map(this::mapUser);
  }

  public void save(User user) {
    UserEntity entity = mapUser(user);
    repository.save(entity);
  }

  private User mapUser(UserEntity entity) {
    return new User(entity.getUsername(), entity.getPassword());
  }

  private UserEntity mapUser(User user) {
    return new UserEntity(null, user.getUsername(), user.getPassword());
  }
}
