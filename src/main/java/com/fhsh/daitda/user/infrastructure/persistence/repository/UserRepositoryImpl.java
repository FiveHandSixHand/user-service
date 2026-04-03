package com.fhsh.daitda.user.infrastructure.persistence.repository;

import com.fhsh.daitda.user.domain.entity.User;
import com.fhsh.daitda.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaUserRepository;

    @Override
    public Optional<User> findById(UUID userId) {
        return jpaUserRepository.findById(userId);
    }

    @Override
    public User save(User user) {
        return jpaUserRepository.save(user);
    }
}
