// src/test/java/org/example/medreservationsystem/model/PatientModelTest.java
package org.example.medreservationsystem.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PatientModelTest {

    @Test
    void equalsAndHashCode_sameFields_areEqual() {
        Patient p1 = new Patient();
        p1.setId(55L);
        p1.setUsername("pat1");
        p1.setEmail("pat1@example.com");
        p1.setPassword("pwd2");
        p1.getRoles().add(User.ROLE_PATIENT);
        p1.setNotificationChannel(NotificationChannel.EMAIL);
        p1.setFirstName("Alice");
        p1.setLastName("Wonder");
        p1.setPhoneNumber("+48115556677");

        Patient p2 = new Patient();
        p2.setId(55L);
        p2.setUsername("pat1");
        p2.setEmail("pat1@example.com");
        p2.setPassword("pwd2");
        p2.getRoles().add(User.ROLE_PATIENT);
        p2.setNotificationChannel(NotificationChannel.EMAIL);
        p2.setFirstName("Alice");
        p2.setLastName("Wonder");
        p2.setPhoneNumber("+48115556677");

        assertEquals(p1, p2, "Patients with identical fields should be equal");
        assertEquals(p1.hashCode(), p2.hashCode(), "Their hashCodes should match");
    }

    @Test
    void equals_differentLastName_notEqual() {
        Patient a = new Patient();
        a.setLastName("One");
        Patient b = new Patient();
        b.setLastName("Two");
        assertNotEquals(a, b, "Different lastName → not equal");
    }

    @Test
    void equals_reflexive() {
        Patient p = new Patient();
        assertEquals(p, p, "An object must equal itself");
    }

    @Test
    void equals_null_returnsFalse() {
        Patient p = new Patient();
        assertNotEquals(null, p, "Comparison with null must return false");
    }

    @Test
    void equals_differentType_returnsFalse() {
        Patient p = new Patient();
        Integer x = 5;
        assertFalse(p.equals(x), "Different types should not be equal");
    }

    @Test
    void getterSetter_fields() {
        Patient p = new Patient();
        p.setFirstName("Bob");
        p.setLastName("Builder");
        p.setPhoneNumber("+48117778899");

        assertEquals("Bob", p.getFirstName());
        assertEquals("Builder", p.getLastName());
        assertEquals("+48117778899", p.getPhoneNumber());
    }

    @Test
    void toString_includesFirstNameAndLastName() {
        Patient p = new Patient();
        p.setFirstName("Clara");
        p.setLastName("Oswald");

        String repr = p.toString();
        assertTrue(repr.contains("Clara"), "toString() should contain firstName");
        assertTrue(repr.contains("Oswald"), "toString() should contain lastName");
    }
}
