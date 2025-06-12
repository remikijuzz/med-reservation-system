// src/test/java/org/example/medreservationsystem/controller/PatientControllerTest.java

package org.example.medreservationsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.medreservationsystem.dto.PatientRequest;
import org.example.medreservationsystem.model.Patient;
import org.example.medreservationsystem.service.PatientService;
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

@WebMvcTest(PatientController.class)
@AutoConfigureMockMvc(addFilters = false)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper;

    private Patient makePatient(Long id) {
        Patient p = new Patient();
        p.setId(id);
        p.setUsername("pat" + id);
        p.setEmail("pat" + id + "@example.com");
        p.setPassword(null);
        p.setFirstName("First" + id);
        p.setLastName("Last" + id);
        p.setPhoneNumber("555" + id);
        return p;
    }

    private PatientRequest makeRequest() {
        PatientRequest req = new PatientRequest();
        req.setUsername("newpat");
        req.setEmail("newpat@example.com");
        req.setPassword("pass");
        req.setFirstName("New");
        req.setLastName("Pat");
        req.setPhoneNumber("123123123");
        req.setNotificationChannel(null);
        return req;
    }

    @Test
    @DisplayName("GET /api/patients → 200 OK, list of patients")
    void getAllPatients_returnsList() throws Exception {
        List<Patient> list = List.of(makePatient(1L), makePatient(2L));
        when(patientService.getAllPatients()).thenReturn(list);

        mockMvc.perform(get("/api/patients"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[1].id").value(2));

        verify(patientService).getAllPatients();
    }

    @Test
    @DisplayName("GET /api/patients/{id} → 200 OK when found")
    void getPatientById_found() throws Exception {
        Patient p = makePatient(5L);
        when(patientService.getPatientById(5L)).thenReturn(p);

        mockMvc.perform(get("/api/patients/5"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(5))
            .andExpect(jsonPath("$.username").value("pat5"));

        verify(patientService).getPatientById(5L);
    }

    @Test
    @DisplayName("GET /api/patients/{id} → 404 Not Found when missing")
    void getPatientById_notFound() throws Exception {
        when(patientService.getPatientById(99L)).thenReturn(null);

        mockMvc.perform(get("/api/patients/99"))
            .andExpect(status().isNotFound());

        verify(patientService).getPatientById(99L);
    }

    @Test
    @DisplayName("POST /api/patients → 201 Created, patient returned")
    void createPatient_success() throws Exception {
        PatientRequest req = makeRequest();
        Patient saved = makePatient(10L);
        when(patientService.savePatient(any(Patient.class))).thenReturn(saved);

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(10))
            .andExpect(jsonPath("$.username").value("pat10"))
            .andExpect(jsonPath("$.password").doesNotExist());

        verify(patientService).savePatient(any(Patient.class));
    }

    @Test
    @DisplayName("PUT /api/patients/{id} → 200 OK when found")
    void updatePatient_found() throws Exception {
        PatientRequest req = makeRequest();
        Patient updated = makePatient(20L);
        when(patientService.updatePatient(eq(20L), any(Patient.class))).thenReturn(updated);

        mockMvc.perform(put("/api/patients/20")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(20));

        verify(patientService).updatePatient(eq(20L), any(Patient.class));
    }

    @Test
    @DisplayName("PUT /api/patients/{id} → 404 Not Found when missing")
    void updatePatient_notFound() throws Exception {
        when(patientService.updatePatient(eq(30L), any(Patient.class))).thenReturn(null);

        mockMvc.perform(put("/api/patients/30")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(makeRequest())))
            .andExpect(status().isNotFound());

        verify(patientService).updatePatient(eq(30L), any(Patient.class));
    }

    @Test
    @DisplayName("DELETE /api/patients/{id} → 204 No Content when deleted")
    void deletePatient_found() throws Exception {
        when(patientService.deletePatient(40L)).thenReturn(true);

        mockMvc.perform(delete("/api/patients/40"))
            .andExpect(status().isNoContent());

        verify(patientService).deletePatient(40L);
    }

    @Test
    @DisplayName("DELETE /api/patients/{id} → 404 Not Found when missing")
    void deletePatient_notFound() throws Exception {
        when(patientService.deletePatient(50L)).thenReturn(false);

        mockMvc.perform(delete("/api/patients/50"))
            .andExpect(status().isNotFound());

        verify(patientService).deletePatient(50L);
    }
}
