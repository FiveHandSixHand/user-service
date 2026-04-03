package com.fhsh.daitda.user.application.service.query;

import com.fhsh.daitda.exception.BusinessException;
import com.fhsh.daitda.user.application.result.UserQueryResult;
import com.fhsh.daitda.user.domain.exception.UserErrorCode;
import com.fhsh.daitda.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {
    private final UserRepository userRepository;

    public UserQueryResult getUserById(UUID userId) {
        return userRepository.findById(userId)
                .map(UserQueryResult::from)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
    }
}
