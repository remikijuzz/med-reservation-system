package org.example.medreservationsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.medreservationsystem.model.Appointment;
import org.example.medreservationsystem.model.Doctor;
import org.example.medreservationsystem.model.Patient;
import org.example.medreservationsystem.service.AppointmentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AppointmentService appointmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private Doctor makeDoctor() {
        Doctor d = new Doctor();
        d.setId(1L);
        d.setFirstName("John");
        d.setLastName("Doe");
        d.setSpecialization("Cardiology");
        d.setEmail("john.doe@example.com");
        d.setPhone("+123456789");
        return d;
    }

    private Patient makePatient() {
        Patient p = new Patient();
        p.setId(1L);
        p.setFirstName("Alice");
        p.setLastName("Wonderland");
        p.setEmail("alice@example.com");
        p.setPhone("+1122334455");
        return p;
    }

    @Test
    void createAppointment_shouldReturnCreated() throws Exception {
        Doctor doctor = makeDoctor();
        Patient patient = makePatient();

        Appointment input = new Appointment();
        input.setDate(LocalDateTime.now().plusDays(1));
        input.setDoctor(doctor);
        input.setPatient(patient);

        Appointment saved = new Appointment();
        saved.setId(1L);
        saved.setDate(input.getDate());
        saved.setDoctor(doctor);
        saved.setPatient(patient);

        when(appointmentService.saveAppointment(any(Appointment.class))).thenReturn(saved);

        mockMvc.perform(post("/api/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.doctor.id").value(1L))
                .andExpect(jsonPath("$.patient.id").value(1L));

        Mockito.verify(appointmentService).saveAppointment(any(Appointment.class));
    }

    @Test
    void getAllAppointments_shouldReturnList() throws Exception {
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDate(LocalDateTime.now().plusDays(1));
        appointment.setDoctor(makeDoctor());
        appointment.setPatient(makePatient());

        when(appointmentService.getAllAppointments()).thenReturn(Collections.singletonList(appointment));

        mockMvc.perform(get("/api/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].doctor.id").value(1L));

        Mockito.verify(appointmentService).getAllAppointments();
    }

    @Test
    void getAppointmentById_whenExists_shouldReturnAppointment() throws Exception {
        Appointment appointment = new Appointment();
        appointment.setId(1L);
        appointment.setDate(LocalDateTime.now().plusDays(1));
        appointment.setDoctor(makeDoctor());
        appointment.setPatient(makePatient());

        when(appointmentService.getAppointmentById(1L)).thenReturn(appointment);

        mockMvc.perform(get("/api/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.patient.id").value(1L));

        Mockito.verify(appointmentService).getAppointmentById(1L);
    }

    @Test
    void updateAppointment_whenExists_shouldReturnOk() throws Exception {
        LocalDateTime newDate = LocalDateTime.now().plusDays(2);
        Appointment input = new Appointment();
        input.setDate(newDate);

        Appointment updated = new Appointment();
        updated.setId(1L);
        updated.setDate(newDate);
        updated.setDoctor(makeDoctor());
        updated.setPatient(makePatient());

        when(appointmentService.updateAppointment(eq(1L), any(Appointment.class))).thenReturn(updated);

        mockMvc.perform(put("/api/appointments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").exists());

        Mockito.verify(appointmentService).updateAppointment(eq(1L), any(Appointment.class));
    }

    @Test
    void deleteAppointment_whenExists_shouldReturnNoContent() throws Exception {
        when(appointmentService.deleteAppointment(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/appointments/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(appointmentService).deleteAppointment(1L);
    }

    @Test
    void deleteAppointment_whenNotExists_shouldReturnNotFound() throws Exception {
        when(appointmentService.deleteAppointment(2L)).thenReturn(false);

        mockMvc.perform(delete("/api/appointments/2"))
                .andExpect(status().isNotFound());

        Mockito.verify(appointmentService).deleteAppointment(2L);
    }
}
