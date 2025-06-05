package org.example.medreservationsystem.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class PatientTest {

    @Test
    void gettersAndSetters_shouldWork() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("Alice");
        patient.setLastName("Wonderland");
        patient.setEmail("alice@example.com");
        patient.setPhone("+1122334455");

        assertThat(patient.getId()).isEqualTo(1L);
        assertThat(patient.getFirstName()).isEqualTo("Alice");
        assertThat(patient.getLastName()).isEqualTo("Wonderland");
        assertThat(patient.getEmail()).isEqualTo("alice@example.com");
        assertThat(patient.getPhone()).isEqualTo("+1122334455");
    }

    @Test
    void equalsAndHashCode_shouldUseAllFields() {
        Patient p1 = new Patient();
        p1.setId(1L);
        p1.setFirstName("Alice");
        p1.setLastName("Wonderland");
        p1.setEmail("alice@example.com");
        p1.setPhone("+1122334455");

        Patient p2 = new Patient();
        p2.setId(1L);
        p2.setFirstName("Alice");
        p2.setLastName("Wonderland");
        p2.setEmail("alice@example.com");
        p2.setPhone("+1122334455");

        Patient p3 = new Patient();
        p3.setId(2L);
        p3.setFirstName("Bob");
        p3.setLastName("Builder");
        p3.setEmail("bob@example.com");
        p3.setPhone("+5566778899");

        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
        assertThat(p1).isNotEqualTo(p3);
    }

    @Test
    void toString_shouldIncludeFields() {
        Patient patient = new Patient();
        patient.setId(5L);
        patient.setFirstName("Charlie");
        patient.setLastName("Delta");
        patient.setEmail("charlie.delta@example.com");
        patient.setPhone("+444555666");

        String str = patient.toString();
        assertThat(str).contains("Charlie");
        assertThat(str).contains("Delta");
        assertThat(str).contains("charlie.delta@example.com");
        assertThat(str).contains("+444555666");
    }
}
