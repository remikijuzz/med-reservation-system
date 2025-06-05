package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.Appointment;
import org.example.medreservationsystem.model.Doctor;
import org.example.medreservationsystem.model.Patient;
import org.example.medreservationsystem.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private AppointmentService appointmentService;

    @Test
    void saveAppointment_shouldSaveAndNotify() {
        Doctor doctor = new Doctor();
        doctor.setFirstName("John");
        doctor.setLastName("Doe");

        Patient patient = new Patient();
        patient.setFirstName("Alice");
        patient.setLastName("Wonderland");

        Appointment appointment = new Appointment();
        appointment.setDate(LocalDateTime.now().plusDays(1));
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);

        when(appointmentRepository.save(appointment)).thenAnswer(invocation -> {
            Appointment a = invocation.getArgument(0);
            a.setId(1L);
            return a;
        });

        Appointment saved = appointmentService.saveAppointment(appointment);

        assertThat(saved.getId()).isEqualTo(1L);
        verify(appointmentRepository).save(appointment);
        verify(notificationService).sendNotification(saved);
    }

    @Test
    void getAllAppointments_shouldReturnList() {
        Appointment appointment = new Appointment();
        appointment.setId(1L);

        List<Appointment> list = Collections.singletonList(appointment);
        when(appointmentRepository.findAll()).thenReturn(list);

        List<Appointment> result = appointmentService.getAllAppointments();

        assertThat(result).hasSize(1).contains(appointment);
        verify(appointmentRepository).findAll();
    }

    @Test
    void getAppointmentById_whenExists_shouldReturnAppointment() {
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        Appointment found = appointmentService.getAppointmentById(1L);

        assertThat(found).isEqualTo(appointment);
        verify(appointmentRepository).findById(1L);
    }

    @Test
    void getAppointmentById_whenNotExists_shouldReturnNull() {
        when(appointmentRepository.findById(2L)).thenReturn(Optional.empty());

        Appointment found = appointmentService.getAppointmentById(2L);

        assertThat(found).isNull();
        verify(appointmentRepository).findById(2L);
    }

    @Test
    void updateAppointment_whenExists_shouldUpdateAndNotify() {
        Appointment existing = new Appointment();
        existing.setId(1L);
        existing.setDate(LocalDateTime.now().plusDays(1));

        Doctor doctor = new Doctor();
        doctor.setFirstName("John");
        doctor.setLastName("Doe");

        Patient patient = new Patient();
        patient.setFirstName("Alice");
        patient.setLastName("Wonderland");

        existing.setDoctor(doctor);
        existing.setPatient(patient);

        LocalDateTime newDate = LocalDateTime.now().plusDays(2);
        Appointment details = new Appointment();
        details.setDate(newDate);
        details.setDoctor(doctor);
        details.setPatient(patient);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Appointment updated = appointmentService.updateAppointment(1L, details);

        assertThat(updated.getDate()).isEqualTo(newDate);
        verify(appointmentRepository).findById(1L);
        verify(appointmentRepository).save(existing);
        verify(notificationService).sendNotification(updated);
    }

    @Test
    void updateAppointment_whenNotExists_shouldReturnNull() {
        Appointment details = new Appointment();
        details.setDate(LocalDateTime.now().plusDays(2));

        when(appointmentRepository.findById(2L)).thenReturn(Optional.empty());

        Appointment updated = appointmentService.updateAppointment(2L, details);

        assertThat(updated).isNull();
        verify(appointmentRepository).findById(2L);
        verify(appointmentRepository, never()).save(any());
        verify(notificationService, never()).sendNotification(any());
    }

    @Test
    void deleteAppointment_whenExists_shouldReturnTrueAndDelete() {
        Appointment appointment = new Appointment();
        appointment.setId(1L);

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));

        boolean result = appointmentService.deleteAppointment(1L);

        assertThat(result).isTrue();
        verify(appointmentRepository).findById(1L);
        verify(appointmentRepository).delete(appointment);
    }

    @Test
    void deleteAppointment_whenNotExists_shouldReturnFalse() {
        when(appointmentRepository.findById(2L)).thenReturn(Optional.empty());

        boolean result = appointmentService.deleteAppointment(2L);

        assertThat(result).isFalse();
        verify(appointmentRepository).findById(2L);
        verify(appointmentRepository, never()).delete(any());
    }
}
