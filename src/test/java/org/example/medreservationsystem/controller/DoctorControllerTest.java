// src/test/java/org/example/medreservationsystem/controller/DoctorControllerTest.java

package org.example.medreservationsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.medreservationsystem.dto.DoctorRequest;
import org.example.medreservationsystem.model.Doctor;
import org.example.medreservationsystem.service.DoctorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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

    private Doctor makeDoctor(Long id) {
        Doctor d = new Doctor();
        d.setId(id);
        d.setUsername("doc"+id);
        d.setEmail("doc"+id+"@example.com");
        d.setPassword(null);
        d.setFirstName("First"+id);
        d.setLastName("Last"+id);
        d.setSpecialization("Spec"+id);
        d.setPhoneNumber("000"+id);
        return d;
    }

    private DoctorRequest makeRequest() {
        DoctorRequest req = new DoctorRequest();
        req.setUsername("newdoc");
        req.setEmail("newdoc@example.com");
        req.setPassword("pass");
        req.setFirstName("New");
        req.setLastName("Doc");
        req.setSpecialization("Cardio");
        req.setPhoneNumber("123456789");
        req.setNotificationChannel(null);
        return req;
    }

    @Test
    @DisplayName("GET /api/doctors → 200 OK, list of doctors")
    void getAllDoctors_returnsList() throws Exception {
        List<Doctor> list = List.of(makeDoctor(1L), makeDoctor(2L));
        when(doctorService.getAllDoctors()).thenReturn(list);

        mockMvc.perform(get("/api/doctors"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[1].id").value(2));

        verify(doctorService).getAllDoctors();
    }

    @Test
    @DisplayName("GET /api/doctors/{id} → 200 OK when found")
    void getDoctorById_found() throws Exception {
        Doctor d = makeDoctor(5L);
        when(doctorService.getDoctorById(5L)).thenReturn(d);

        mockMvc.perform(get("/api/doctors/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.username").value("doc5"));

        verify(doctorService).getDoctorById(5L);
    }

    @Test
    @DisplayName("GET /api/doctors/{id} → 404 Not Found when missing")
    void getDoctorById_notFound() throws Exception {
        when(doctorService.getDoctorById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/doctors/99"))
            .andExpect(status().isNotFound());

        verify(doctorService).getDoctorById(99L);
    }

    @Test
    @DisplayName("POST /api/doctors → 201 Created, doctor returned")
    void createDoctor_success() throws Exception {
        DoctorRequest req = makeRequest();
        Doctor saved = makeDoctor(10L);
        when(doctorService.saveDoctor(any(Doctor.class))).thenReturn(saved);

        mockMvc.perform(post("/api/doctors")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.username").value("doc10"))
            .andExpect(jsonPath("$.password").doesNotExist());

        verify(doctorService).saveDoctor(any(Doctor.class));
    }

    @Test
    @DisplayName("PUT /api/doctors/{id} → 200 OK when found")
    void updateDoctor_found() throws Exception {
        DoctorRequest req = makeRequest();
        Doctor updated = makeDoctor(20L);
        when(doctorService.updateDoctor(eq(20L), any(Doctor.class))).thenReturn(updated);

        mockMvc.perform(put("/api/doctors/20")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(20));

        verify(doctorService).updateDoctor(eq(20L), any(Doctor.class));
    }

    @Test
    @DisplayName("PUT /api/doctors/{id} → 404 Not Found when missing")
    void updateDoctor_notFound() throws Exception {
        when(doctorService.updateDoctor(eq(30L), any(Doctor.class))).thenReturn(null);

        mockMvc.perform(put("/api/doctors/30")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(makeRequest())))
            .andExpect(status().isNotFound());

        verify(doctorService).updateDoctor(eq(30L), any(Doctor.class));
    }

    @Test
    @DisplayName("DELETE /api/doctors/{id} → 204 No Content when deleted")
    void deleteDoctor_found() throws Exception {
        when(doctorService.deleteDoctor(40L)).thenReturn(true);

        mockMvc.perform(delete("/api/doctors/40"))
            .andExpect(status().isNoContent());

        verify(doctorService).deleteDoctor(40L);
    }

    @Test
    @DisplayName("DELETE /api/doctors/{id} → 404 Not Found when missing")
    void deleteDoctor_notFound() throws Exception {
        when(doctorService.deleteDoctor(50L)).thenReturn(false);

        mockMvc.perform(delete("/api/doctors/50"))
            .andExpect(status().isNotFound());

        verify(doctorService).deleteDoctor(50L);
    }
}
