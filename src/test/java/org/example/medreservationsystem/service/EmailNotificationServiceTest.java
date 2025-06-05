package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.Appointment;
import org.example.medreservationsystem.model.Doctor;
import org.example.medreservationsystem.model.Patient;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EmailNotificationServiceTest {

    private final EmailNotificationService emailService = new EmailNotificationService();

    @Test
    void sendNotification_shouldNotThrow() {
        // Przygotowanie przykładowej wizyty
        Doctor doctor = new Doctor();
        doctor.setFirstName("John");
        doctor.setLastName("Doe");

        Patient patient = new Patient();
        patient.setFirstName("Alice");
        patient.setLastName("Wonderland");

        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDate(LocalDateTime.now().plusDays(1));
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);

        // Wywołanie metody, upewniamy się, że nie rzuca wyjątku
        assertDoesNotThrow(() -> emailService.sendNotification(appointment));
    }
}
