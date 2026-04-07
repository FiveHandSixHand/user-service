package com.fhsh.daitda.user.domain.repository;

import com.fhsh.daitda.user.application.command.UserSearchCommand;
import com.fhsh.daitda.user.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserCustomRepository {
    Page<User> findAll(UserSearchCommand command, Pageable pageable);
}
