package org.example.medreservationsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.medreservationsystem.model.Doctor;
import org.example.medreservationsystem.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoctorController.class)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorService doctorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void saveDoctor_asAdmin_shouldReturnCreated() throws Exception {
        Doctor input = new Doctor();
        input.setFirstName("John");
        input.setLastName("Doe");
        input.setSpecialization("Cardiology");
        input.setEmail("john.doe@example.com");
        input.setPhone("+123456789");

        Doctor saved = new Doctor();
        saved.setId(1L);
        saved.setFirstName("John");
        saved.setLastName("Doe");
        saved.setSpecialization("Cardiology");
        saved.setEmail("john.doe@example.com");
        saved.setPhone("+123456789");

        when(doctorService.saveDoctor(any(Doctor.class))).thenReturn(saved);

        mockMvc.perform(post("/api/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));

        verify(doctorService).saveDoctor(any(Doctor.class));
    }

    @Test
    void saveDoctor_withoutAuth_shouldReturnForbidden() throws Exception {
        Doctor input = new Doctor();
        input.setFirstName("John");
        input.setLastName("Doe");

        mockMvc.perform(post("/api/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "DOCTOR"})
    void getAllDoctors_asAuthorized_shouldReturnList() throws Exception {
        Doctor doc = new Doctor();
        doc.setId(1L);
        doc.setFirstName("John");
        doc.setLastName("Doe");
        doc.setSpecialization("Cardiology");
        doc.setEmail("john.doe@example.com");
        doc.setPhone("+123456789");

        when(doctorService.getAllDoctors()).thenReturn(Collections.singletonList(doc));

        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));

        verify(doctorService).getAllDoctors();
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void getDoctorById_asPatient_shouldReturnDoctor() throws Exception {
        Doctor doc = new Doctor();
        doc.setId(1L);
        doc.setFirstName("John");
        doc.setLastName("Doe");
        doc.setSpecialization("Cardiology");
        doc.setEmail("john.doe@example.com");
        doc.setPhone("+123456789");

        when(doctorService.getDoctorById(1L)).thenReturn(doc);

        mockMvc.perform(get("/api/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.specialization").value("Cardiology"));

        verify(doctorService).getDoctorById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateDoctor_asAdmin_shouldReturnUpdated() throws Exception {
        Doctor input = new Doctor();
        input.setFirstName("Jane");
        input.setLastName("Smith");
        input.setSpecialization("Neurology");
        input.setEmail("jane.smith@example.com");
        input.setPhone("+987654321");

        Doctor updated = new Doctor();
        updated.setId(1L);
        updated.setFirstName("Jane");
        updated.setLastName("Smith");
        updated.setSpecialization("Neurology");
        updated.setEmail("jane.smith@example.com");
        updated.setPhone("+987654321");

        when(doctorService.updateDoctor(eq(1L), any(Doctor.class))).thenReturn(updated);

        mockMvc.perform(put("/api/doctors/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.email").value("jane.smith@example.com"));

        verify(doctorService).updateDoctor(eq(1L), any(Doctor.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteDoctor_asAdmin_shouldReturnNoContent() throws Exception {
        when(doctorService.deleteDoctor(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/doctors/1"))
                .andExpect(status().isNoContent());

        verify(doctorService).deleteDoctor(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteDoctor_whenNotExists_shouldReturnNotFound() throws Exception {
        when(doctorService.deleteDoctor(2L)).thenReturn(false);

        mockMvc.perform(delete("/api/doctors/2"))
                .andExpect(status().isNotFound());

        verify(doctorService).deleteDoctor(2L);
    }
}
