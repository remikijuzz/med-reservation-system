package org.example.medreservationsystem.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class DoctorTest {

    @Test
    void gettersAndSetters_shouldWork() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFirstName("Alice");
        doctor.setLastName("Brown");
        doctor.setSpecialization("Dermatology");
        doctor.setEmail("alice.brown@example.com");
        doctor.setPhone("+111222333");

        assertThat(doctor.getId()).isEqualTo(1L);
        assertThat(doctor.getFirstName()).isEqualTo("Alice");
        assertThat(doctor.getLastName()).isEqualTo("Brown");
        assertThat(doctor.getSpecialization()).isEqualTo("Dermatology");
        assertThat(doctor.getEmail()).isEqualTo("alice.brown@example.com");
        assertThat(doctor.getPhone()).isEqualTo("+111222333");
    }

    @Test
    void equalsAndHashCode_shouldUseId() {
        Doctor d1 = new Doctor();
        d1.setId(1L);
        d1.setFirstName("Alice");

        Doctor d2 = new Doctor();
        d2.setId(1L);
        d2.setFirstName("Alice");

        Doctor d3 = new Doctor();
        d3.setId(2L);
        d3.setFirstName("Bob");

        assertThat(d1).isEqualTo(d2);
        assertThat(d1.hashCode()).isEqualTo(d2.hashCode());
        assertThat(d1).isNotEqualTo(d3);
    }

    @Test
    void toString_shouldIncludeFields() {
        Doctor doctor = new Doctor();
        doctor.setId(5L);
        doctor.setFirstName("Charlie");
        doctor.setLastName("Delta");
        doctor.setSpecialization("Orthopedics");
        doctor.setEmail("charlie.delta@example.com");
        doctor.setPhone("+444555666");

        String str = doctor.toString();
        assertThat(str).contains("Charlie");
        assertThat(str).contains("Orthopedics");
        assertThat(str).contains("charlie.delta@example.com");
    }
}
