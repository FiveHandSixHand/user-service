package com.fhsh.daitda.user.domain.entity;

import com.fhsh.daitda.user.domain.enums.UserRole;
import com.fhsh.daitda.user.domain.enums.UserStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    @DisplayName("User Entity 생성 테스트")
    void createUser() {
        String email = "test@test.com";
        String name = "Test User";
        UserRole role = UserRole.MASTER;
        UUID hubId = UUID.randomUUID();

        User user = User.builder()
                .email(email)
                .name(name)
                .role(role)
                .hubId(hubId)
                .build();

        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getRole()).isEqualTo(role);
        assertThat(user.getHubId()).isEqualTo(hubId);
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING); // 기본값으로 되었는지 확인
    }

    @Test
    @DisplayName("유저 승인 테스트")
    void approveUser() {
        // given
        User user = User.builder()
                .email("test@test.com")
                .name("Test User")
                .role(UserRole.MASTER)
                .build();

        user.approve();

        assertThat(user.getStatus()).isEqualTo(UserStatus.APPROVED);
    }

    @Test
    @DisplayName("유저 거절 테스트")
    void rejectUser() {
        User user = User.builder()
                .email("test@test.com")
                .name("Test User")
                .role(UserRole.MASTER)
                .build();

        user.reject();

        assertThat(user.getStatus()).isEqualTo(UserStatus.REJECTED);
    }
}
