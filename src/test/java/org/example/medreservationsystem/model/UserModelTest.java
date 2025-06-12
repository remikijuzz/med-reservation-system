package org.example.medreservationsystem.model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserModelTest {

    @Test
    void equalsAndHashCode_sameFields_areEqual() {
        User u1 = new User();
        u1.setId(42L);
        u1.setUsername("userX");
        u1.setEmail("user@example.com");
        u1.setPassword("secret");
        u1.getRoles().add(User.ROLE_USER);
        u1.setNotificationChannel(NotificationChannel.SMS);

        User u2 = new User();
        u2.setId(42L);
        u2.setUsername("userX");
        u2.setEmail("user@example.com");
        u2.setPassword("secret");
        u2.getRoles().add(User.ROLE_USER);
        u2.setNotificationChannel(NotificationChannel.SMS);

        assertEquals(u1, u2, "Users with identical fields should be equal");
        assertEquals(u1.hashCode(), u2.hashCode(), "Their hashCodes should match");
    }

    @Test
    void equals_reflexive() {
        User u = new User();
        u.setUsername("alice");
        assertEquals(u, u, "An object must equal itself");
    }

    @Test
    void equals_null_returnsFalse() {
        User u = new User();
        assertNotEquals(null, u, "Comparison with null must return false");
    }

    @Test
    void equals_differentType_returnsFalse() {
        User u = new User();
        String notAUser = "not a user";
        assertFalse(u.equals(notAUser), "Different types should not be equal");
    }

    @Test
    void equals_differentId_notEqual() {
        User a = new User();
        a.setId(1L);
        User b = new User();
        b.setId(2L);
        assertNotEquals(a, b, "Different IDs → objects should not be equal");
    }

    @Test
    void equals_differentUsername_notEqual() {
        User a = new User();
        a.setUsername("alice");
        User b = new User();
        b.setUsername("bob");
        assertNotEquals(a, b, "Different usernames → objects should not be equal");
    }

    @Test
    void defaultNotificationChannel_isEmail() {
        User u = new User();
        assertEquals(NotificationChannel.EMAIL, u.getNotificationChannel(),
                     "Default notification channel should be EMAIL");
    }

    @Test
    void notificationChannel_setterGetter() {
        User u = new User();
        u.setNotificationChannel(NotificationChannel.SMS);
        assertEquals(NotificationChannel.SMS, u.getNotificationChannel(),
                     "Setter/getter for notificationChannel should work");
    }

    @Test
    void rolesCollection_isMutable() {
        User u = new User();
        assertNotNull(u.getRoles(), "Roles set should never be null");
        int before = u.getRoles().size();
        u.getRoles().add(User.ROLE_ADMIN);
        assertTrue(u.getRoles().contains(User.ROLE_ADMIN), "Should be able to add a role");
        u.getRoles().remove(User.ROLE_ADMIN);
        assertEquals(before, u.getRoles().size(), "Roles set size should return to initial");
    }

    @Test
    void password_setterGetter() {
        User u = new User();
        u.setPassword("myPass");
        assertEquals("myPass", u.getPassword(), "Setter/getter for password should work");
    }

    @Test
    void toString_includesUsernameAndEmail() {
        User u = new User();
        u.setUsername("john");
        u.setEmail("john@doe.com");
        String repr = u.toString();
        assertTrue(repr.contains("john"), "toString() should contain username");
        assertTrue(repr.contains("john@doe.com"), "toString() should contain email");
    }
}
