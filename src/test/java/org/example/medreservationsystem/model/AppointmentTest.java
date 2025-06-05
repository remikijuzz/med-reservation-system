package org.example.medreservationsystem.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class AppointmentTest {

    private Doctor sampleDoctor() {
        Doctor d = new Doctor();
        d.setId(1L);
        d.setFirstName("John");
        d.setLastName("Doe");
        d.setSpecialization("Cardiology");
        d.setEmail("john.doe@example.com");
        d.setPhone("+123456789");
        return d;
    }

    private Patient samplePatient() {
        Patient p = new Patient();
        p.setId(2L);
        p.setFirstName("Alice");
        p.setLastName("Wonderland");
        p.setEmail("alice@example.com");
        p.setPhone("+1122334455");
        return p;
    }

    @Test
    void gettersAndSetters_shouldWork() {
        Appointment appointment = new Appointment();
        appointment.setId(10L);
        LocalDateTime date = LocalDateTime.of(2025, 7, 1, 9, 30);
        appointment.setDate(date);
        appointment.setDoctor(sampleDoctor());
        appointment.setPatient(samplePatient());

        assertThat(appointment.getId()).isEqualTo(10L);
        assertThat(appointment.getDate()).isEqualTo(date);
        assertThat(appointment.getDoctor()).isEqualTo(sampleDoctor());
        assertThat(appointment.getPatient()).isEqualTo(samplePatient());
    }

    @Test
    void equalsAndHashCode_shouldUseAllFields() {
        LocalDateTime date = LocalDateTime.of(2025, 7, 1, 9, 30);

        Appointment a1 = new Appointment();
        a1.setId(10L);
        a1.setDate(date);
        a1.setDoctor(sampleDoctor());
        a1.setPatient(samplePatient());

        Appointment a2 = new Appointment();
        a2.setId(10L);
        a2.setDate(date);
        a2.setDoctor(sampleDoctor());
        a2.setPatient(samplePatient());

        Appointment a3 = new Appointment();
        a3.setId(11L);
        a3.setDate(date.plusDays(1));
        a3.setDoctor(sampleDoctor());
        a3.setPatient(samplePatient());

        assertThat(a1).isEqualTo(a2);
        assertThat(a1.hashCode()).isEqualTo(a2.hashCode());
        assertThat(a1).isNotEqualTo(a3);
    }

    @Test
    void toString_shouldIncludeFields() {
        LocalDateTime date = LocalDateTime.of(2025, 7, 1, 9, 30);

        Appointment appointment = new Appointment();
        appointment.setId(20L);
        appointment.setDate(date);
        appointment.setDoctor(sampleDoctor());
        appointment.setPatient(samplePatient());

        String str = appointment.toString();
        assertThat(str).contains("20");
        assertThat(str).contains("2025-07-01T09:30");
        assertThat(str).contains("john.doe@example.com");
        assertThat(str).contains("alice@example.com");
    }
}
