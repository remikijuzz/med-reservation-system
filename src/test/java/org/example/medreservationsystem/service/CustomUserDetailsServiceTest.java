package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.User;
import org.example.medreservationsystem.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_whenUserExists_shouldReturnUserDetails() {
        User user = new User() {};
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(User.ROLE_USER);

        when(userRepository.findByUsername("testuser")).thenReturn(user);

        UserDetails userDetails = userDetailsService.loadUserByUsername("testuser");

        assertThat(userDetails.getUsername()).isEqualTo("testuser");
        assertThat(userDetails.getPassword()).isEqualTo("password");
        assertThat(userDetails.getAuthorities())
                .extracting("authority")
                .containsExactly(User.ROLE_USER);
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void loadUserByUsername_whenUserNotFound_shouldThrow() {
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Nie znaleziono użytkownika: unknown");
        verify(userRepository).findByUsername("unknown");
    }
}
