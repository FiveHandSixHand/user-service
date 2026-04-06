package com.fhsh.daitda.user.domain.entity;

import com.fhsh.daitda.domain.BaseUserEntity;
import com.fhsh.daitda.exception.BusinessException;
import com.fhsh.daitda.user.domain.enums.UserRole;
import com.fhsh.daitda.user.domain.enums.UserStatus;
import com.fhsh.daitda.user.domain.exception.UserErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Entity
@Table(name = "p_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUserEntity {
    @Id
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 20, nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private UserStatus status;

    @Column(name = "slack_user_id", length = 50, nullable = false)
    private String slackUserId;

    @Column(name = "hub_id")
    private UUID hubId;

    @Column(name = "company_id")
    private UUID companyId;

    @Builder(access = AccessLevel.PRIVATE)
    private User(UUID userId, String email, String name, UserRole role, String slackUserId, UUID hubId, UUID companyId) {
        validateEmail(email);
        validateName(name);

        this.userId = userId;
        this.email = email;
        this.name = name;
        this.role = role;
        this.slackUserId = slackUserId;
        this.hubId = hubId;
        this.companyId = companyId;
        this.status = UserStatus.PENDING;
    }

    public static User create(
            UUID userId, // Keycloak에서 받은 UUID
            String email,
            String name,
            UserRole role,
            String slackUserId,
            UUID hubId,
            UUID companyId
    ) {
        return User.builder()
                .userId(userId)
                .email(email)
                .name(name)
                .role(role)
                .slackUserId(slackUserId)
                .hubId(hubId)
                .companyId(companyId)
                .build();
    }

    public void approve() {
        this.status = UserStatus.APPROVED;
    }

    public void reject() {
        this.status = UserStatus.REJECTED;
    }

    @Override
    public void delete(UUID deletedBy) {
        super.delete(deletedBy);
        this.status = UserStatus.DELETED;
    }

    @Override
    public void restore(UUID restoredBy) {
        super.restore(restoredBy);
        this.approve();
    }

    private void validateEmail(String email) {
        if (!StringUtils.hasText(email) || !email.contains("@")) {
            throw new BusinessException(UserErrorCode.INVALID_EMAIL_FORMAT);
        }
    }

    private void validateName(String name) {
        if (!StringUtils.hasText(name) || name.length() < 2) {
            throw new BusinessException(UserErrorCode.INVALID_USER_INPUT, "이름은 최소 2자 이상이어야 합니다.");
        }
    }
}
