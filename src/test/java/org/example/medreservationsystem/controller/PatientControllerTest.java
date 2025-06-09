package org.example.medreservationsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.medreservationsystem.model.Patient;
import org.example.medreservationsystem.service.PatientService;
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

@WebMvcTest(PatientController.class)
@AutoConfigureMockMvc(addFilters = false)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PatientService patientService;

    private Patient samplePatient() {
        Patient p = new Patient();
        p.setId(1L);
        p.setFirstName("Alice");
        p.setLastName("Wonderland");
        p.setEmail("alice@example.com");
        p.setPhone("+1122334455");
        return p;
    }

    @Test
    void createPatient_shouldReturnCreated() throws Exception {
        Patient input = samplePatient();
        input.setId(null);

        Patient saved = samplePatient();
        when(patientService.savePatient(any(Patient.class))).thenReturn(saved);

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void getAllPatients_shouldReturnList() throws Exception {
        Patient p = samplePatient();
        when(patientService.getAllPatients()).thenReturn(Collections.singletonList(p));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].phone").value("+1122334455"));
    }

    @Test
    void getPatientById_whenExists_shouldReturnPatient() throws Exception {
        Patient p = samplePatient();
        when(patientService.getPatientById(1L)).thenReturn(p);

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.lastName").value("Wonderland"));
    }

    @Test
    void updatePatient_whenExists_shouldReturnOk() throws Exception {
        Patient input = samplePatient();
        input.setFirstName("Bob");
        input.setLastName("Builder");

        Patient updated = samplePatient();
        updated.setFirstName("Bob");
        updated.setLastName("Builder");

        when(patientService.updatePatient(eq(1L), any(Patient.class))).thenReturn(updated);

        mockMvc.perform(put("/api/patients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Bob"))
                .andExpect(jsonPath("$.lastName").value("Builder"));
    }

    @Test
    void deletePatient_whenExists_shouldReturnNoContent() throws Exception {
        when(patientService.deletePatient(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePatient_whenNotExists_shouldReturnNotFound() throws Exception {
        when(patientService.deletePatient(2L)).thenReturn(false);

        mockMvc.perform(delete("/api/patients/2"))
                .andExpect(status().isNotFound());
    }
}
