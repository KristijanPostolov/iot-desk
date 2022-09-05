package com.kristijan.iotdesk.jpa.jparepositories;

import com.kristijan.iotdesk.jpa.models.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepositoryJpa extends JpaRepository<UserEntity, Long> {

  Optional<UserEntity> findByUsername(String username);
}
