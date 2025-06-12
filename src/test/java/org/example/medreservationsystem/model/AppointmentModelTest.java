// src/test/java/org/example/medreservationsystem/model/AppointmentModelTest.java
package org.example.medreservationsystem.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentModelTest {

    @Test
    void equalsAndHashCode_sameFields_areEqual() {
        Patient p = new Patient(); p.setId(1L);
        Doctor  d = new Doctor();  d.setId(2L);
        LocalDateTime dt = LocalDateTime.of(2025, 6, 20, 9, 30);

        Appointment a1 = new Appointment();
        a1.setId(10L);
        a1.setAppointmentDateTime(dt);
        a1.setPatient(p);
        a1.setDoctor(d);
        a1.setDescription("checkup");

        Appointment a2 = new Appointment();
        a2.setId(10L);
        a2.setAppointmentDateTime(dt);
        a2.setPatient(p);
        a2.setDoctor(d);
        a2.setDescription("checkup");

        assertEquals(a1, a2, "Appointments with identical fields must be equal");
        assertEquals(a1.hashCode(), a2.hashCode(), "Their hashCodes must match");
    }

    @Test
    void equals_reflexive_nullAndDifferentType() {
        Appointment appt = new Appointment();
        assertEquals(appt, appt, "An object must equal itself");
        assertNotEquals(null, appt, "An object must not equal null");
        assertFalse(appt.equals("foo"), "An object must not equal a different type");
    }

    @Test
    void equals_differentId_notEqual() {
        Appointment a1 = new Appointment(); a1.setId(1L);
        Appointment a2 = new Appointment(); a2.setId(2L);
        assertNotEquals(a1, a2, "Different IDs → not equal");
    }

    @Test
    void equals_differentDescription_notEqual() {
        Appointment a1 = new Appointment(); a1.setDescription("a");
        Appointment a2 = new Appointment(); a2.setDescription("b");
        assertNotEquals(a1, a2, "Different descriptions → not equal");
    }

    @Test
    void getterSetter_fields() {
        Appointment appt = new Appointment();
        LocalDateTime dt = LocalDateTime.now();
        appt.setAppointmentDateTime(dt);
        appt.setDescription("hello");

        assertEquals(dt, appt.getAppointmentDateTime());
        assertEquals("hello", appt.getDescription());

        // test id too
        appt.setId(99L);
        assertEquals(99L, appt.getId());
    }

    @Test
    void toString_includesDescriptionAndDateTime() {
        Appointment appt = new Appointment();
        LocalDateTime dt = LocalDateTime.of(2025, 1, 1, 12, 0);
        appt.setAppointmentDateTime(dt);
        appt.setDescription("annual");

        String repr = appt.toString();
        assertTrue(repr.contains("annual"), "toString() should include description");
        assertTrue(repr.contains("2025-01-01T12:00"), "toString() should include date/time");
    }
}
