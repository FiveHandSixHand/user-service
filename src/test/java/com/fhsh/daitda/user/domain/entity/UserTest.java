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
        String slackUserId = "U12345678";
        UUID hubId = UUID.randomUUID();

        User user = User.create(email, name, role, slackUserId, hubId, null);

        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getName()).isEqualTo(name);
        assertThat(user.getRole()).isEqualTo(role);
        assertThat(user.getSlackUserId()).isEqualTo(slackUserId);
        assertThat(user.getHubId()).isEqualTo(hubId);
        assertThat(user.getStatus()).isEqualTo(UserStatus.PENDING); // 기본값으로 되었는지 확인
    }

    @Test
    @DisplayName("유저 승인 테스트")
    void approveUser() {
        // given
        User user = User.create("test@test.com", "Test User", UserRole.MASTER, "SLACK_ID", null, null);

        user.approve();

        assertThat(user.getStatus()).isEqualTo(UserStatus.APPROVED);
    }

    @Test
    @DisplayName("유저 거절 테스트")
    void rejectUser() {
        User user = User.create("test@test.com", "Test User", UserRole.MASTER, "SLACK_ID", null, null);

        user.reject();

        assertThat(user.getStatus()).isEqualTo(UserStatus.REJECTED);
    }

    @Test
    @DisplayName("유저 삭제 테스트 - UserStatus 동기화 확인")
    void deleteUser() {
        // given
        User user = User.create("test@test.com", "Test User", UserRole.MASTER, "SLACK_ID", null, null);

        // when
        user.delete("ADMIN");

        // then
        assertThat(user.getStatus()).isEqualTo(UserStatus.DELETED);
        assertThat(user.isDeleted()).isTrue();
        assertThat(user.getDeletedBy()).isEqualTo("ADMIN");
    }
}
