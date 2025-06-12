// src/test/java/org/example/medreservationsystem/model/ConstructorCoverageTest.java
package org.example.medreservationsystem.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ConstructorCoverageTest {

    @Test
    void userAllArgsConstructor_andGetters() {
        Set<String> roles = Set.of(User.ROLE_USER, User.ROLE_ADMIN);
        User u = new User(
            7L,
            "u7",
            "u7@example.com",
            "pw7",
            roles,
            NotificationChannel.SMS
        );

        assertEquals(7L, u.getId());
        assertEquals("u7", u.getUsername());
        assertEquals("u7@example.com", u.getEmail());
        assertEquals("pw7", u.getPassword());
        assertEquals(NotificationChannel.SMS, u.getNotificationChannel());
        assertTrue(u.getRoles().containsAll(Set.of(User.ROLE_USER, User.ROLE_ADMIN)));
    }

    @Test
    void doctorCustomConstructor_setsRoleAndFields() {
        Doctor d = new Doctor(
            "docX",
            "docx@ex.com",
            "passX",
            "Anna",
            "Nowak",
            "Pediatrics",
            "+48123123123",
            NotificationChannel.EMAIL
        );

        // z konstruktora wychodzi już z dodaną rolą ROLE_DOCTOR
        assertEquals("docX", d.getUsername());
        assertEquals("docx@ex.com", d.getEmail());
        assertEquals("Anna", d.getFirstName());
        assertEquals("Nowak", d.getLastName());
        assertEquals("Pediatrics", d.getSpecialization());
        assertEquals("+48123123123", d.getPhoneNumber());
        assertEquals(NotificationChannel.EMAIL, d.getNotificationChannel());
        assertTrue(d.getRoles().contains(User.ROLE_DOCTOR), "Doctor must have ROLE_DOCTOR");
    }

    @Test
    void patientCustomConstructor_setsRoleAndFields() {
        Patient p = new Patient(
            "patY",
            "paty@ex.com",
            "passY",
            "Bob",
            "Builder",
            "+48987654321",
            NotificationChannel.SMS
        );

        assertEquals("patY", p.getUsername());
        assertEquals("paty@ex.com", p.getEmail());
        assertEquals("Bob", p.getFirstName());
        assertEquals("Builder", p.getLastName());
        assertEquals("+48987654321", p.getPhoneNumber());
        assertEquals(NotificationChannel.SMS, p.getNotificationChannel());
        assertTrue(p.getRoles().contains(User.ROLE_PATIENT), "Patient must have ROLE_PATIENT");
    }

    @Test
    void appointmentAllArgsConstructor_andGetters() {
        Patient p = new Patient(); p.setId(2L);
        Doctor  d = new Doctor();  d.setId(3L);
        LocalDateTime dt = LocalDateTime.of(2025, 12, 1, 8, 45);

        Appointment a = new Appointment(
            11L,
            dt,
            p,
            d,
            "special exam"
        );

        assertEquals(11L, a.getId());
        assertEquals(dt, a.getAppointmentDateTime());
        assertSame(p, a.getPatient());
        assertSame(d, a.getDoctor());
        assertEquals("special exam", a.getDescription());
    }
}
