// src/test/java/org/example/medreservationsystem/service/AuthServiceTest.java

package org.example.medreservationsystem.service;

import org.example.medreservationsystem.dto.LoginRequest;
import org.example.medreservationsystem.dto.RegisterRequest;
import org.example.medreservationsystem.model.Doctor;
import org.example.medreservationsystem.model.NotificationChannel;
import org.example.medreservationsystem.model.Patient;
import org.example.medreservationsystem.model.User;
import org.example.medreservationsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_success() {
        // given
        RegisterRequest req = new RegisterRequest(
            "user1",
            "user1@example.com",
            "secret",
            NotificationChannel.EMAIL
        );
        when(userRepository.existsByUsername("user1")).thenReturn(false);
        when(userRepository.existsByEmail("user1@example.com")).thenReturn(false);
        // return the same instance that service creates (with roles set)
        when(userRepository.save(any(User.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        User result = authService.registerUser(req);

        // then
        assertNotNull(result);
        assertEquals("user1", result.getUsername());
        assertTrue(result.getRoles().contains(User.ROLE_USER));
        verify(passwordEncoder).encode("secret");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_usernameConflict_throws() {
        // given
        when(userRepository.existsByUsername("dup")).thenReturn(true);
        RegisterRequest req = new RegisterRequest(
            "dup",
            "dup@example.com",
            "pass",
            null
        );

        // when / then
        ResponseStatusException ex = assertThrows(
            ResponseStatusException.class,
            () -> authService.registerUser(req)
        );
        assertEquals(409, ex.getStatus().value());
    }

    @Test
    void registerAdmin_success() {
        // given
        RegisterRequest req = new RegisterRequest(
            "admin",
            "admin@example.com",
            "adminpw",
            NotificationChannel.SMS
        );
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(userRepository.existsByEmail("admin@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        User result = authService.registerAdmin(req);

        // then
        assertNotNull(result);
        assertTrue(result.getRoles().contains(User.ROLE_USER));
        assertTrue(result.getRoles().contains(User.ROLE_ADMIN));
        verify(passwordEncoder).encode("adminpw");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerPatient_success() {
        // given
        RegisterRequest req = new RegisterRequest(
            "pat1",
            "pat1@example.com",
            "pw",
            NotificationChannel.EMAIL
        );
        when(userRepository.existsByUsername("pat1")).thenReturn(false);
        when(userRepository.existsByEmail("pat1@example.com")).thenReturn(false);
        when(userRepository.save(any(Patient.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        Patient result = authService.registerPatient(
            req, "Jan", "Kowalski", "+48123123123"
        );

        // then
        assertNotNull(result);
        assertEquals("pat1", result.getUsername());
        assertTrue(result.getRoles().contains(User.ROLE_PATIENT));
        verify(passwordEncoder).encode("pw");
        verify(userRepository).save(any(Patient.class));
    }

    @Test
    void registerDoctor_success() {
        // given
        RegisterRequest req = new RegisterRequest(
            "doc1",
            "doc1@example.com",
            "pw2",
            null
        );
        when(userRepository.existsByUsername("doc1")).thenReturn(false);
        when(userRepository.existsByEmail("doc1@example.com")).thenReturn(false);
        when(userRepository.save(any(Doctor.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        Doctor result = authService.registerDoctor(
            req, "Anna", "Nowak", "Cardio", "+48987654321"
        );

        // then
        assertNotNull(result);
        assertEquals("doc1", result.getUsername());
        assertTrue(result.getRoles().contains(User.ROLE_DOCTOR));
        verify(passwordEncoder).encode("pw2");
        verify(userRepository).save(any(Doctor.class));
    }

    @Test
    void login_success_returnsToken() {
        // given
        LoginRequest loginReq = new LoginRequest("user1", "pw");
        User user = new User();
        user.setUsername("user1");
        user.setPassword("encoded");
        when(userRepository.findByUsername("user1")).thenReturn(user);
        when(passwordEncoder.matches("pw", "encoded")).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        // when
        String token = authService.login(loginReq);

        // then
        assertEquals("jwt-token", token);
    }

    @Test
    void login_badCredentials_throws() {
        // given
        when(userRepository.findByUsername("userX")).thenReturn(null);
        LoginRequest loginReq = new LoginRequest("userX", "wrong");

        // when / then
        assertThrows(
            BadCredentialsException.class,
            () -> authService.login(loginReq)
        );
    }
}
