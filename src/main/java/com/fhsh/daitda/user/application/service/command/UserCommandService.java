package com.fhsh.daitda.user.application.service.command;

import com.fhsh.daitda.user.application.command.UserSignupCommand;
import com.fhsh.daitda.user.domain.entity.User;
import com.fhsh.daitda.user.domain.repository.UserRepository;
import com.fhsh.daitda.user.application.port.AccountProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final AccountProvider accountProvider;
    private final UserRepository userRepository;

    @Transactional
    public UUID signup(UserSignupCommand command) {
        // Keycloak 계정 생성 (DB 트랜잭션과 무관한 외부 통신)
        UUID keycloakId = accountProvider.createAccount(
                command.email(),
                command.password(),
                command.name(),
                command.role()
        );

        try {
            // 엔티티 생성
            User user = User.create(
                    keycloakId,
                    command.email(),
                    command.name(),
                    command.role(),
                    command.slackUserId(),
                    command.hubId(),
                    command.companyId()
            );

            // DB 저장
            return userRepository.save(user).getUserId();
        } catch (Exception e) {
            // 회원가입 중 실패하면 keycloak_db에서도 삭제
            accountProvider.deleteAccount(keycloakId);
            throw e; 
        }
    }
}
