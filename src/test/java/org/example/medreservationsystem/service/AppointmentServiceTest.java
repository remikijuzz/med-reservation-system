package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.*;
import org.example.medreservationsystem.notification.NotificationService;
import org.example.medreservationsystem.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock(name = "EMAIL")
    private NotificationService emailService;
    @Mock(name = "SMS")
    private NotificationService smsService;

    private AppointmentService appointmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Map<String, NotificationService> notificationMap = new HashMap<>();
        notificationMap.put("EMAIL", emailService);
        notificationMap.put("SMS", smsService);
        appointmentService = new AppointmentService(
            appointmentRepository,
            patientRepository,
            doctorRepository,
            notificationMap
        );
    }

    @Test
    void createAppointment_success_sendsEmailNotification() {
        // given
        Long patientId = 1L, doctorId = 2L;
        Patient patient = new Patient();
        patient.setId(patientId);
        patient.setNotificationChannel(NotificationChannel.EMAIL);

        Doctor doctor = new Doctor();
        doctor.setId(doctorId);
        doctor.setLastName("Kowalski");

        LocalDateTime dateTime = LocalDateTime.of(2025, 6, 20, 9, 30);
        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        Appointment saved = new Appointment(5L, dateTime, patient, doctor, "Badanie kontrolne");
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(saved);

        // when
        Appointment result = appointmentService.createAppointment(
            patientId, doctorId, dateTime, "Badanie kontrolne"
        );

        // then
        assertNotNull(result);
        assertEquals(5L, result.getId());
        verify(emailService, times(1))
            .sendNotification(eq(patient), contains("Kowalski"));
    }

    @Test
    void createAppointment_patientNotFound_throwsException() {
        // given
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when / then
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
            appointmentService.createAppointment(99L, 1L, LocalDateTime.now(), "Desc")
        );
        assertTrue(ex.getMessage().contains("Nie znaleziono pacjenta"));
    }

    @Test
    void updateAppointment_success_sendsEmailNotification() {
        // given
        Long apptId = 3L;
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setNotificationChannel(NotificationChannel.EMAIL);
        Doctor doctor = new Doctor();
        doctor.setId(2L);
        doctor.setLastName("Nowak");
        LocalDateTime oldDate = LocalDateTime.of(2025, 6, 10, 10, 0);
        Appointment existing = new Appointment(apptId, oldDate, patient, doctor, "Stara");

        when(appointmentRepository.findById(apptId)).thenReturn(Optional.of(existing));

        LocalDateTime newDate = LocalDateTime.of(2025, 6, 11, 11, 30);
        Appointment updated = new Appointment(apptId, newDate, patient, doctor, "Nowa");
        when(appointmentRepository.save(existing)).thenReturn(updated);

        // when
        Appointment result = appointmentService.updateAppointment(
            apptId, null, null, newDate, "Nowa"
        );

        // then
        assertNotNull(result);
        assertEquals(newDate, result.getAppointmentDateTime());
        assertEquals("Nowa", result.getDescription());
        verify(emailService, times(1))
            .sendNotification(eq(patient), contains("została zaktualizowana"));
    }

    @Test
    void updateAppointment_notFound_returnsNull() {
        // given
        Long apptId = 10L;
        when(appointmentRepository.findById(apptId)).thenReturn(Optional.empty());

        // when
        Appointment result = appointmentService.updateAppointment(
            apptId, null, null, LocalDateTime.now(), "X"
        );

        // then
        assertNull(result);
        verifyNoInteractions(emailService, smsService);
    }

    @Test
    void deleteAppointment_success_sendsSmsNotification() {
        // given
        Long apptId = 4L;
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setNotificationChannel(NotificationChannel.SMS);
        Doctor doctor = new Doctor();
        doctor.setId(2L);
        doctor.setLastName("Krol");
        LocalDateTime date = LocalDateTime.of(2025, 6, 12, 14, 0);
        Appointment appt = new Appointment(apptId, date, patient, doctor, "Desc");

        when(appointmentRepository.findById(apptId)).thenReturn(Optional.of(appt));

        // when
        boolean deleted = appointmentService.deleteAppointment(apptId);

        // then
        assertTrue(deleted);
        verify(smsService, times(1))
            .sendNotification(eq(patient), contains("została odwołana"));
        verify(appointmentRepository, times(1)).delete(appt);
    }

    @Test
    void deleteAppointment_notFound_returnsFalse() {
        // given
        when(appointmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        boolean deleted = appointmentService.deleteAppointment(99L);

        // then
        assertFalse(deleted);
        verifyNoInteractions(emailService, smsService);
    }

    @Test
    void getAppointmentsByPatient_returnsList() {
        // given
        Long patientId = 7L;
        Appointment appt1 = mock(Appointment.class);
        Appointment appt2 = mock(Appointment.class);
        when(appointmentRepository.findByPatientId(patientId))
            .thenReturn(List.of(appt1, appt2));

        // when
        List<Appointment> list = appointmentService.getAppointmentsByPatient(patientId);

        // then
        assertEquals(2, list.size());
        assertTrue(list.contains(appt1) && list.contains(appt2));
    }

    @Test
    void getAppointmentsByDoctor_returnsList() {
        // given
        Long doctorId = 8L;
        Appointment appt1 = mock(Appointment.class);
        when(appointmentRepository.findByDoctorId(doctorId))
            .thenReturn(List.of(appt1));

        // when
        List<Appointment> list = appointmentService.getAppointmentsByDoctor(doctorId);

        // then
        assertEquals(1, list.size());
        assertEquals(appt1, list.get(0));
    }
}
