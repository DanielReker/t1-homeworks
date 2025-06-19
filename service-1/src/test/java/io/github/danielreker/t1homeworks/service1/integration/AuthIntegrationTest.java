package io.github.danielreker.t1homeworks.service1.integration;

import io.github.danielreker.t1homeworks.service1.model.User;
import io.github.danielreker.t1homeworks.service1.model.dto.JwtResponse;
import io.github.danielreker.t1homeworks.service1.model.dto.LoginRequest;
import io.github.danielreker.t1homeworks.service1.model.dto.SignupRequest;
import io.github.danielreker.t1homeworks.service1.model.enums.RoleEnum;
import io.github.danielreker.t1homeworks.service1.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void signup_shouldRegisterUser() throws Exception {
        SignupRequest signupRequest = new SignupRequest(
                "testuser",
                "test@example.com",
                Set.of("user", "mod"),
                "password123"
        );

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User testuser registered successfully!"));

        User savedUser = userRepository.findByLogin("testuser").orElseThrow();
        assertThat(savedUser.getEmail()).isEqualTo("test@example.com");
        assertThat(passwordEncoder.matches("password123", savedUser.getPassword())).isTrue();
        assertThat(savedUser.getRoles()).contains(RoleEnum.ROLE_USER, RoleEnum.ROLE_MODERATOR);
    }

    @Test
    void signin_shouldAuthenticateAndReturnJwt() throws Exception {
        User existingUser = User.builder()
                .login("auth-user")
                .email("auth@example.com")
                .password(passwordEncoder.encode("strong-password"))
                .roles(Set.of(RoleEnum.ROLE_ADMIN))
                .build();
        userRepository.save(existingUser);

        LoginRequest loginRequest = new LoginRequest("auth-user", "strong-password");

        String responseBody = mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.username").value("auth-user"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_ADMIN"))
                .andReturn().getResponse().getContentAsString();

        JwtResponse jwtResponse = objectMapper.readValue(responseBody, JwtResponse.class);
        assertThat(jwtResponse.getAccessToken()).isNotNull().startsWith("ey");
    }
}