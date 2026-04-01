package com.fhsh.daitda.user.domain.entity;

import com.fhsh.daitda.user.domain.enums.UserRole;
import com.fhsh.daitda.user.domain.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id", updatable = false, nullable = false)
    private UUID userId;

    @Column(name = "email", length = 30, nullable = false, unique = true)
    private String email;

    @Column(name = "name", length = 10, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 10, nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 10, nullable = false)
    private UserStatus status;

    @Column(name = "slack_user_id", length = 50)
    private String slackUserId;

    @Column(name = "hub_id")
    private UUID hubId;

    @Column(name = "company_id")
    private UUID companyId;

    public void approve() {
        this.status = UserStatus.APPROVED;
    }

    public void reject() {
        this.status = UserStatus.REJECTED;
    }
}
