package com.fhsh.daitda.user.domain.entity;

import com.fhsh.daitda.domain.BaseUserEntity;
import com.fhsh.daitda.user.domain.enums.UserRole;
import com.fhsh.daitda.user.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseUserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
    private User(String email, String name, UserRole role, String slackUserId, UUID hubId, UUID companyId) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.slackUserId = slackUserId;
        this.hubId = hubId;
        this.companyId = companyId;
        this.status = UserStatus.PENDING;
    }

    public static User create(
            String email,
            String name,
            UserRole role,
            String slackUserId,
            UUID hubId,
            UUID companyId
    ) {
        return User.builder()
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
    public void delete(String deletedBy) {
        super.delete(deletedBy);
        this.status = UserStatus.DELETED;
    }
}
