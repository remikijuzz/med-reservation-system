// src/test/java/org/example/medreservationsystem/model/DoctorModelTest.java
package org.example.medreservationsystem.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DoctorModelTest {

    @Test
    void equalsAndHashCode_sameFields_areEqual() {
        Doctor d1 = new Doctor();
        d1.setId(100L);
        d1.setUsername("doc1");
        d1.setEmail("doc1@example.com");
        d1.setPassword("pwd");
        d1.getRoles().add(User.ROLE_DOCTOR);
        d1.setNotificationChannel(NotificationChannel.SMS);
        d1.setFirstName("John");
        d1.setLastName("Smith");
        d1.setSpecialization("Cardio");
        d1.setPhoneNumber("+48123456789");

        Doctor d2 = new Doctor();
        d2.setId(100L);
        d2.setUsername("doc1");
        d2.setEmail("doc1@example.com");
        d2.setPassword("pwd");
        d2.getRoles().add(User.ROLE_DOCTOR);
        d2.setNotificationChannel(NotificationChannel.SMS);
        d2.setFirstName("John");
        d2.setLastName("Smith");
        d2.setSpecialization("Cardio");
        d2.setPhoneNumber("+48123456789");

        assertEquals(d1, d2, "Doctors with identical fields should be equal");
        assertEquals(d1.hashCode(), d2.hashCode(), "Their hashCodes should match");
    }

    @Test
    void equals_differentSpecialization_notEqual() {
        Doctor a = new Doctor();
        a.setSpecialization("Derm");
        Doctor b = new Doctor();
        b.setSpecialization("Cardio");
        assertNotEquals(a, b, "Different specialization → not equal");
    }

    @Test
    void equals_reflexive() {
        Doctor d = new Doctor();
        assertEquals(d, d, "An object must equal itself");
    }

    @Test
    void equals_null_returnsFalse() {
        Doctor d = new Doctor();
        assertNotEquals(null, d, "Comparison with null must return false");
    }

    @Test
    void equals_differentType_returnsFalse() {
        Doctor d = new Doctor();
        String s = "not a doctor";
        assertFalse(d.equals(s), "Different types should not be equal");
    }

    @Test
    void getterSetter_fields() {
        Doctor d = new Doctor();
        d.setFirstName("Anna");
        d.setLastName("Nowak");
        d.setSpecialization("Pediatrics");
        d.setPhoneNumber("+48987654321");

        assertEquals("Anna", d.getFirstName());
        assertEquals("Nowak", d.getLastName());
        assertEquals("Pediatrics", d.getSpecialization());
        assertEquals("+48987654321", d.getPhoneNumber());
    }

    @Test
    void toString_includesFirstNameAndSpecialization() {
        Doctor d = new Doctor();
        d.setFirstName("Eva");
        d.setSpecialization("Dermatology");

        String repr = d.toString();
        assertTrue(repr.contains("Eva"), "toString() should contain firstName");
        assertTrue(repr.contains("Dermatology"), "toString() should contain specialization");
    }
}
