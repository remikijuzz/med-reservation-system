package org.example.medreservationsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.medreservationsystem.dto.AppointmentRequest;
import org.example.medreservationsystem.model.Appointment;
import org.example.medreservationsystem.model.Doctor;
import org.example.medreservationsystem.model.NotificationChannel;
import org.example.medreservationsystem.model.Patient;
import org.example.medreservationsystem.model.User;
import org.example.medreservationsystem.repository.UserRepository;
import org.example.medreservationsystem.service.AppointmentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    @MockBean
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper mapper;

    private Appointment sampleAppointment() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setUsername("patient1");
        patient.setRoles(Set.of(User.ROLE_PATIENT));
        patient.setNotificationChannel(NotificationChannel.EMAIL);
        patient.setFirstName("Alice");
        patient.setLastName("Wonder");
        patient.setPhoneNumber("111");

        Doctor doctor = new Doctor();
        doctor.setId(2L);
        doctor.setUsername("doctor1");
        doctor.setRoles(Set.of(User.ROLE_DOCTOR));
        doctor.setNotificationChannel(NotificationChannel.EMAIL);
        doctor.setFirstName("Doc");
        doctor.setLastName("Tor");
        doctor.setSpecialization("Gen");
        doctor.setPhoneNumber("222");

        Appointment a = new Appointment();
        a.setId(5L);
        a.setAppointmentDateTime(LocalDateTime.of(2025, 6, 20, 9, 30));
        a.setPatient(patient);
        a.setDoctor(doctor);
        a.setDescription("desc");
        return a;
    }

    @Test
    @DisplayName("POST /api/appointments – tworzy wizytę i zwraca 201")
    void createAppointment_success() throws Exception {
        var req = new AppointmentRequest();
        req.setPatientId(1L);
        req.setDoctorId(2L);
        req.setAppointmentDateTime(LocalDateTime.of(2025, 6, 20, 9, 30));
        req.setDescription("desc");

        Appointment saved = sampleAppointment();
        Mockito.when(appointmentService.createAppointment(
                eq(1L), eq(2L),
                any(LocalDateTime.class), eq("desc")))
            .thenReturn(saved);

        mockMvc.perform(post("/api/appointments")
                .with(user("patient1").roles("PATIENT"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.description").value("desc"));
    }

    @Test
    @DisplayName("GET /api/appointments/5 – ADMIN widzi każdą wizytę")
    void getById_asAdmin() throws Exception {
        Mockito.when(appointmentService.getAppointmentById(5L))
               .thenReturn(sampleAppointment());

        mockMvc.perform(get("/api/appointments/5")
                .with(user("admin").roles("ADMIN")))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    @DisplayName("GET /api/appointments/5 – DOCTOR widzi tylko swoje")
    void getById_asDoctor_otherForbidden() throws Exception {
        Mockito.when(appointmentService.getAppointmentById(5L))
               .thenReturn(sampleAppointment());

        mockMvc.perform(get("/api/appointments/5")
                .with(user("doctor2").roles("DOCTOR")))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/appointments/5 – PATIENT widzi tylko swoje")
    void getById_asPatient_ownSuccess() throws Exception {
        Mockito.when(appointmentService.getAppointmentById(5L))
               .thenReturn(sampleAppointment());

        mockMvc.perform(get("/api/appointments/5")
                .with(user("patient1").roles("PATIENT")))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT /api/appointments/5 – PATIENT aktualizuje swoją wizytę")
    void updateAppointment_asPatient_success() throws Exception {
        Appointment existing = sampleAppointment();
        Mockito.when(appointmentService.getAppointmentById(5L))
               .thenReturn(existing);
        Mockito.when(appointmentService.updateAppointment(
                eq(5L), any(), any(), any(), any()))
               .thenReturn(existing);

        AppointmentRequest req = new AppointmentRequest();
        req.setPatientId(1L);
        req.setDoctorId(2L);
        req.setAppointmentDateTime(existing.getAppointmentDateTime());
        req.setDescription("desc");

        mockMvc.perform(put("/api/appointments/5")
                .with(user("patient1").roles("PATIENT"))
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    @DisplayName("DELETE /api/appointments/5 – tylko ADMIN lub właściciel")
    void deleteAppointment_forbiddenForOtherPatient() throws Exception {
        Appointment existing = sampleAppointment();
        Mockito.when(appointmentService.getAppointmentById(5L))
               .thenReturn(existing);

        mockMvc.perform(delete("/api/appointments/5")
                .with(user("patient2").roles("PATIENT"))
                .with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/appointments/patient/1 – PATIENT widzi swoje, inni nie")
    void getByPatient_authChecks() throws Exception {
        Mockito.when(appointmentService.getAppointmentsByPatient(1L))
               .thenReturn(List.of(sampleAppointment()));
        // stubujemy userRepository
        User u1 = new User(); u1.setId(1L); u1.setUsername("patient1");
        User u2 = new User(); u2.setId(2L); u2.setUsername("patient2");
        Mockito.when(userRepository.findByUsername("patient1")).thenReturn(u1);
        Mockito.when(userRepository.findByUsername("patient2")).thenReturn(u2);

        // patient1 → OK
        mockMvc.perform(get("/api/appointments/patient/1")
                .with(user("patient1").roles("PATIENT")))
            .andExpect(status().isOk());

        // patient2 → FORBIDDEN
        mockMvc.perform(get("/api/appointments/patient/1")
                .with(user("patient2").roles("PATIENT")))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/appointments/doctor/2 – tylko ADMIN lub doctor2")
    void getByDoctor_authChecks() throws Exception {
        Mockito.when(appointmentService.getAppointmentsByDoctor(2L))
               .thenReturn(List.of(sampleAppointment()));
        // stubujemy userRepository
        User d1 = new User(); d1.setId(1L); d1.setUsername("doctor1");
        User d2 = new User(); d2.setId(2L); d2.setUsername("doctor2");
        Mockito.when(userRepository.findByUsername("doctor1")).thenReturn(d1);
        Mockito.when(userRepository.findByUsername("doctor2")).thenReturn(d2);

        // jako doctor2 → OK
        mockMvc.perform(get("/api/appointments/doctor/2")
                .with(user("doctor2").roles("DOCTOR")))
            .andExpect(status().isOk());

        // jako doctor1 → FORBIDDEN
        mockMvc.perform(get("/api/appointments/doctor/2")
                .with(user("doctor1").roles("DOCTOR")))
            .andExpect(status().isForbidden());
    }
}
