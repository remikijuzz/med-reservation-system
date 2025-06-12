// src/test/java/org/example/medreservationsystem/controller/AuthControllerTest.java

package org.example.medreservationsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.medreservationsystem.dto.LoginRequest;
import org.example.medreservationsystem.dto.RegisterRequest;
import org.example.medreservationsystem.model.User;
import org.example.medreservationsystem.service.AuthService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)  // disable Spring Security filters
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/auth/register → 201 CREATED, password omitted")
    void registerUser_success() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("newuser");
        req.setEmail("newuser@example.com");
        req.setPassword("pass123");
        req.setNotificationChannel(null);

        User saved = new User();
        saved.setId(10L);
        saved.setUsername("newuser");
        saved.setEmail("newuser@example.com");
        saved.setPassword("encoded");
        saved.getRoles().add(User.ROLE_USER);
        when(authService.registerUser(any(RegisterRequest.class))).thenReturn(saved);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.username").value("newuser"))
            .andExpect(jsonPath("$.email").value("newuser@example.com"))
            .andExpect(jsonPath("$.password").doesNotExist());

        verify(authService).registerUser(any(RegisterRequest.class));
    }

    @Test
    @DisplayName("POST /api/auth/login → 200 OK + token")
    void loginUser_success() throws Exception {
        LoginRequest req = new LoginRequest("user1","secret");
        when(authService.login(any(LoginRequest.class))).thenReturn("jwt-token-123");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("jwt-token-123"));
    }

    @Test
    @DisplayName("POST /api/auth/login → 401 Unauthorized on bad credentials")
    void loginUser_badCredentials() throws Exception {
        LoginRequest req = new LoginRequest("user1","wrong");
        when(authService.login(any(LoginRequest.class)))
            .thenThrow(new BadCredentialsException("Nieprawidłowe"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error")
                .value("Nieprawidłowy login lub hasło"));
    }

    @Test
    @DisplayName("POST /api/auth/login → 500 on unexpected exception")
    void loginUser_otherException() throws Exception {
        LoginRequest req = new LoginRequest("user1","secret");
        when(authService.login(any(LoginRequest.class)))
            .thenThrow(new RuntimeException("oops"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.error")
                .value("Wystąpił błąd podczas logowania"));
    }
}
