package com.fhsh.daitda.user.application.service.command;
import com.fhsh.daitda.exception.BusinessException;
import com.fhsh.daitda.user.application.command.SignupCommand;
import com.fhsh.daitda.user.application.command.UserRoleUpdateCommand;
import com.fhsh.daitda.user.application.command.UserUpdateCommand;
import com.fhsh.daitda.user.application.result.UserUpdateResult;
import com.fhsh.daitda.user.domain.entity.User;
import com.fhsh.daitda.user.domain.enums.UserStatus;
import com.fhsh.daitda.user.domain.exception.UserErrorCode;
import com.fhsh.daitda.user.domain.repository.UserRepository;
import com.fhsh.daitda.user.application.port.AccountPort;
import com.fhsh.daitda.user.infrastructure.external.feign.CompanyClient;
import com.fhsh.daitda.user.infrastructure.external.feign.HubClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCommandService {

    private final AccountPort accountPort;
    private final UserRepository userRepository;
    private final HubClient hubClient;
    private final CompanyClient companyClient;

    @Transactional
    public void signup(SignupCommand command) {
        // 허브 및 업체 존재 여부 검증
        validateHubAndCompany(command.hubId(), command.companyId());

        // Keycloak 계정 생성 (DB 트랜잭션과 무관한 외부 통신)
        UUID keycloakId = accountPort.createAccount(
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
        } catch (Exception e) {
            // 회원가입 중 실패하면 keycloak_db에서도 삭제
            accountPort.deleteAccount(keycloakId);
            throw e; 
        }
    }

    @Transactional
    public void registration(UUID userId, boolean isApproved) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        // 승인/거절 처리 및 Keycloak 동기화
        if (isApproved) {
            user.approve();
            accountPort.updateAccountStatus(user.getUserId(), true);
        } else {
            user.reject();
            // 거절 시에는 Keycloak 계정을 비활성화 상태로 유지
            accountPort.updateAccountStatus(user.getUserId(), false);
        }

        userRepository.save(user);
    }


    @Transactional
    public void deleteUser(UUID targetUserId, UUID deletedBy) {
        // 1. 대상 사용자 조회
        User user = userRepository.findById(targetUserId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        // 이미 삭제된 상태인지 확인
        if (user.getStatus() == UserStatus.DELETED) {
            throw new BusinessException(UserErrorCode.ALREADY_DELETED);
        }

        // DB Soft Delete 수행
        user.delete(deletedBy);
        userRepository.save(user);

        // Keycloak 계정 삭제
        accountPort.deleteAccount(targetUserId);
    }

    @Transactional
    public UserUpdateResult updateUser(UserUpdateCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (user.getStatus() == UserStatus.DELETED) {
            throw new BusinessException(UserErrorCode.ALREADY_DELETED);
        }

        // 허브 및 업체 존재 여부 검증
        validateHubAndCompany(command.hubId(), command.companyId());

        user.update(command.name(), command.slackUserId(), command.hubId(), command.companyId());
        User updatedUser = userRepository.saveAndFlush(user);

        return new UserUpdateResult(
                updatedUser.getUserId(),
                updatedUser.getName(),
                updatedUser.getSlackUserId(),
                updatedUser.getHubId(),
                updatedUser.getCompanyId(),
                updatedUser.getUpdatedAt()
        );
    }

    @Transactional
    public void updateUserRole(UserRoleUpdateCommand command) {
        User user = userRepository.findById(command.userId())
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        if (user.getStatus() == UserStatus.DELETED) {
            throw new BusinessException(UserErrorCode.ALREADY_DELETED);
        }

        // 로컬 DB 권한 변경
        user.updateRole(command.role());
        userRepository.saveAndFlush(user);

        // Keycloak 권한 변경
        accountPort.updateAccountRole(user.getUserId(), command.role());
    }

    private void validateHubAndCompany(UUID hubId, UUID companyId) {
        if (hubId != null) {
            try {
                hubClient.getHubById(hubId);
            } catch (Exception e) {
                throw new BusinessException(UserErrorCode.INVALID_REFERENCE_ID);
            }
        }
        if (companyId != null) {
            try {
                companyClient.getCompanyById(companyId);
            } catch (Exception e) {
                throw new BusinessException(UserErrorCode.INVALID_REFERENCE_ID);
            }
        }
    }
}
