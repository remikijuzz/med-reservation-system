// src/test/java/org/example/medreservationsystem/service/CustomUserDetailsServiceTest.java

package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.User;
import org.example.medreservationsystem.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userDetailsService = new CustomUserDetailsService(userRepository);
    }

    @Test
    void loadUserByUsername_existingUser_returnsUserDetails() {
        // given
        User user = new User();
        user.setUsername("alice");
        user.setPassword("secret");
        user.getRoles().add("ROLE_USER");
        user.getRoles().add("ROLE_PATIENT");
        when(userRepository.findByUsername("alice")).thenReturn(user);

        // when
        UserDetails ud = userDetailsService.loadUserByUsername("alice");

        // then
        assertEquals("alice", ud.getUsername());
        assertEquals("secret", ud.getPassword());

        Collection<?> auths = ud.getAuthorities();
        assertTrue(auths.contains(new SimpleGrantedAuthority("ROLE_USER")));
        assertTrue(auths.contains(new SimpleGrantedAuthority("ROLE_PATIENT")));
    }

    @Test
    void loadUserByUsername_unknownUser_throwsException() {
        // given
        when(userRepository.findByUsername("unknown")).thenReturn(null);

        // when / then
        UsernameNotFoundException ex = assertThrows(
            UsernameNotFoundException.class,
            () -> userDetailsService.loadUserByUsername("unknown")
        );
        assertTrue(ex.getMessage().contains("Nie znaleziono użytkownika"));
    }
}
