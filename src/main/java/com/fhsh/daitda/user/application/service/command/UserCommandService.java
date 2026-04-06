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

            // saveAndFlush()로 즉시 DB 반영 강제 해야함
            // @Transactional에서 userRepository.save(user)는 영속성 컨텍스트에만 저장하고,
            // 실제 flush와 커밋은 메서드 반환 후에 발생
            // try-catch 블록은 persist 단계까지만 보호하며,
            // 커밋 시 제약조건 위반이나 flush 실패가 발생하면 catch 블록이 실행되지 않아 deleteAccount() 호출이 누락
            User savedUser = userRepository.saveAndFlush(user);
            return savedUser.getUserId();
        } catch (Exception e) {
            // 회원가입 중 실패하면 keycloak_db에서도 삭제
            accountProvider.deleteAccount(keycloakId);
            throw e; 
        }
    }
}
