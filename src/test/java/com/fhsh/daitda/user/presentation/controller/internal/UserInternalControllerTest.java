package com.fhsh.daitda.user.presentation.controller.internal;

import com.fhsh.daitda.user.domain.entity.User;
import com.fhsh.daitda.user.domain.enums.UserRole;
import com.fhsh.daitda.user.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("[Integration] 사용자 정보 조회 API 테스트")
class UserInternalControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 ID로 정보 조회 시 성공하면 200 응답과 데이터를 반환한다")
    void getUserById_Success() throws Exception {
        // given
        UUID userId = UUID.randomUUID();
        String email = "test_user@example.com";
        String name = "테스트사용자";
        UserRole role = UserRole.MASTER;
        String slackUserId = "U12345678";

        User testUser = User.create(
                userId,
                email,
                name,
                role,
                slackUserId,
                UUID.randomUUID(), // hubId
                UUID.randomUUID()  // companyId
        );
        userRepository.save(testUser);

        // when & then
        mockMvc.perform(get("/internal/v1/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.data.userId").value(userId.toString()))
                .andExpect(jsonPath("$.data.email").value(email))
                .andExpect(jsonPath("$.data.name").value(name))
                .andExpect(jsonPath("$.data.role").value(role.name()))
                .andExpect(jsonPath("$.data.slackUserId").value(slackUserId));
    }

    @Test
    @DisplayName("존재하지 않는 사용자 ID로 조회 시 404 에러를 반환한다")
    void getUserById_NotFound() throws Exception {
        // given
        UUID nonExistentUserId = UUID.randomUUID();

        // when & then
        mockMvc.perform(get("/internal/v1/users/{userId}", nonExistentUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
