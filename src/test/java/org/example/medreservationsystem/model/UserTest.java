package org.example.medreservationsystem.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Test
    void gettersAndSetters_shouldWork() {
        // Tworzymy anonimową podklasę, bo User jest abstrakcyjny
        User user = new User() {};
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setRole(User.ROLE_USER);

        assertThat(user.getId()).isEqualTo(1L);
        assertThat(user.getUsername()).isEqualTo("testuser");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(user.getRole()).isEqualTo(User.ROLE_USER);
    }

    @Test
    void equalsAndHashCode_shouldUseAllFields() {
        User u1 = new User() {};
        u1.setId(1L);
        u1.setUsername("testuser");
        u1.setPassword("password123");
        u1.setRole(User.ROLE_USER);

        User u2 = new User() {};
        u2.setId(1L);
        u2.setUsername("testuser");
        u2.setPassword("password123");
        u2.setRole(User.ROLE_USER);

        User u3 = new User() {};
        u3.setId(2L);
        u3.setUsername("other");
        u3.setPassword("pass");
        u3.setRole(User.ROLE_ADMIN);

        assertThat(u1).isEqualTo(u2);
        assertThat(u1.hashCode()).isEqualTo(u2.hashCode());
        assertThat(u1).isNotEqualTo(u3);
    }

    @Test
    void toString_shouldIncludeFields() {
        User user = new User() {};
        user.setId(5L);
        user.setUsername("alice");
        user.setPassword("secret");
        user.setRole(User.ROLE_ADMIN);

        String str = user.toString();
        assertThat(str).contains("alice");
        assertThat(str).contains("ROLE_ADMIN");
        assertThat(str).contains("5");
    }
}
