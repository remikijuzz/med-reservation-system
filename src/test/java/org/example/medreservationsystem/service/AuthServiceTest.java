package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.User;
import org.example.medreservationsystem.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldSetUserRoleAndSave() {
        // Tworzymy anonimową podklasę User, bo User jest abstrakcyjny
        User user = new User() {};
        user.setUsername("user1");
        user.setPassword("pass");
        // rola początkowo nieustawiona

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = authService.register(user);

        assertThat(saved.getRole()).isEqualTo(User.ROLE_USER);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getRole()).isEqualTo(User.ROLE_USER);
    }

    @Test
    void registerAdmin_shouldSetAdminRoleAndSave() {
        User user = new User() {};
        user.setUsername("admin");
        user.setPassword("secret");
        // rola początkowo nieustawiona

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User saved = authService.registerAdmin(user);

        assertThat(saved.getRole()).isEqualTo(User.ROLE_ADMIN);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getRole()).isEqualTo(User.ROLE_ADMIN);
    }

    @Test
    void login_whenCredentialsMatch_shouldReturnDummyToken() {
        User stored = new User() {};
        stored.setId(5L);
        stored.setUsername("user1");
        stored.setPassword("pass");

        // Tworzymy anonimową podklasę User dla próby logowania
        User loginAttempt = new User() {};
        loginAttempt.setUsername("user1");
        loginAttempt.setPassword("pass");

        when(userRepository.findByUsername("user1")).thenReturn(stored);

        String token = authService.login(loginAttempt);

        assertThat(token).isEqualTo("dummy-token-5");
        verify(userRepository).findByUsername("user1");
    }

    @Test
    void login_whenPasswordMismatch_shouldReturnNull() {
        User stored = new User() {};
        stored.setUsername("user1");
        stored.setPassword("correct");

        User loginAttempt = new User() {};
        loginAttempt.setUsername("user1");
        loginAttempt.setPassword("wrong");

        when(userRepository.findByUsername("user1")).thenReturn(stored);

        String token = authService.login(loginAttempt);

        assertThat(token).isNull();
        verify(userRepository).findByUsername("user1");
    }

    @Test
    void login_whenUserNotFound_shouldReturnNull() {
        User loginAttempt = new User() {};
        loginAttempt.setUsername("unknown");
        loginAttempt.setPassword("any");

        when(userRepository.findByUsername("unknown")).thenReturn(null);

        String token = authService.login(loginAttempt);

        assertThat(token).isNull();
        verify(userRepository).findByUsername("unknown");
    }
}
