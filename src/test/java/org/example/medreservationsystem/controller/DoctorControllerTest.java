package org.example.medreservationsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.medreservationsystem.model.Doctor;
import org.example.medreservationsystem.service.DoctorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DoctorController.class)
@AutoConfigureMockMvc(addFilters = false)
class DoctorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DoctorService doctorService;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    void createDoctor_shouldReturnCreated() throws Exception {
        Doctor input = sampleDoctor();
        input.setId(null);

        Doctor saved = sampleDoctor();
        when(doctorService.saveDoctor(any(Doctor.class))).thenReturn(saved);

        mockMvc.perform(post("/api/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("John"));
    }

    @Test
    void getAllDoctors_shouldReturnList() throws Exception {
        Doctor doc = sampleDoctor();
        when(doctorService.getAllDoctors()).thenReturn(Collections.singletonList(doc));

        mockMvc.perform(get("/api/doctors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].email").value("john.doe@example.com"));
    }

    @Test
    void getDoctorById_whenExists_shouldReturnDoctor() throws Exception {
        Doctor doc = sampleDoctor();
        when(doctorService.getDoctorById(1L)).thenReturn(doc);

        mockMvc.perform(get("/api/doctors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.specialization").value("Cardiology"));
    }

    @Test
    void updateDoctor_whenExists_shouldReturnOk() throws Exception {
        Doctor input = sampleDoctor();
        input.setFirstName("Jane");
        input.setLastName("Smith");
        input.setSpecialization("Neurology");

        Doctor updated = sampleDoctor();
        updated.setFirstName("Jane");
        updated.setLastName("Smith");
        updated.setSpecialization("Neurology");

        when(doctorService.updateDoctor(eq(1L), any(Doctor.class))).thenReturn(updated);

        mockMvc.perform(put("/api/doctors/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Jane"))
                .andExpect(jsonPath("$.specialization").value("Neurology"));
    }

    @Test
    void deleteDoctor_whenExists_shouldReturnNoContent() throws Exception {
        when(doctorService.deleteDoctor(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/doctors/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteDoctor_whenNotExists_shouldReturnNotFound() throws Exception {
        when(doctorService.deleteDoctor(2L)).thenReturn(false);

        mockMvc.perform(delete("/api/doctors/2"))
                .andExpect(status().isNotFound());
    }
}
