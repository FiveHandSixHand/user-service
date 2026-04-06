package com.fhsh.daitda.user.domain.repository;

import com.fhsh.daitda.user.domain.entity.User;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    Optional<User> findById(UUID userId);
    User save(User user);
    User saveAndFlush(User user);
}
