package org.example.medreservationsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.medreservationsystem.model.User;
import org.example.medreservationsystem.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void registerUser_shouldReturnCreated() throws Exception {
        User input = new User() {};
        input.setUsername("newuser");
        input.setPassword("pass");

        User saved = new User() {};
        saved.setId(1L);
        saved.setUsername("newuser");
        saved.setPassword("pass");
        saved.setRole(User.ROLE_USER);

        when(authService.register(any(User.class))).thenReturn(saved);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.role").value(User.ROLE_USER));

        verify(authService).register(any(User.class));
    }

    @Test
    void registerAdmin_shouldReturnCreated() throws Exception {
        User input = new User() {};
        input.setUsername("admin");
        input.setPassword("adminpass");

        User saved = new User() {};
        saved.setId(2L);
        saved.setUsername("admin");
        saved.setPassword("adminpass");
        saved.setRole(User.ROLE_ADMIN);

        when(authService.registerAdmin(any(User.class))).thenReturn(saved);

        mockMvc.perform(post("/api/auth/registerAdmin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.role").value(User.ROLE_ADMIN));

        verify(authService).registerAdmin(any(User.class));
    }

    @Test
    void login_whenValid_shouldReturnToken() throws Exception {
        User input = new User() {};
        input.setUsername("testuser");
        input.setPassword("pass");

        when(authService.login(any(User.class))).thenReturn("dummy-token-1");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(content().string("dummy-token-1"));

        verify(authService).login(any(User.class));
    }

    @Test
    void login_whenInvalid_shouldReturnUnauthorized() throws Exception {
        User input = new User() {};
        input.setUsername("testuser");
        input.setPassword("wrong");

        when(authService.login(any(User.class))).thenReturn(null);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isUnauthorized());

        verify(authService).login(any(User.class));
    }
}
