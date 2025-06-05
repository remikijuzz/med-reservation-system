package org.example.medreservationsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.medreservationsystem.model.Patient;
import org.example.medreservationsystem.service.PatientService;
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

@WebMvcTest(PatientController.class)
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void savePatient_asAdmin_shouldReturnCreated() throws Exception {
        Patient input = new Patient();
        input.setFirstName("Alice");
        input.setLastName("Wonderland");
        input.setEmail("alice@example.com");
        input.setPhone("+1122334455");

        Patient saved = new Patient();
        saved.setId(1L);
        saved.setFirstName("Alice");
        saved.setLastName("Wonderland");
        saved.setEmail("alice@example.com");
        saved.setPhone("+1122334455");

        when(patientService.savePatient(any(Patient.class))).thenReturn(saved);

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));

        verify(patientService).savePatient(any(Patient.class));
    }

    @Test
    void savePatient_withoutAuth_shouldReturnForbidden() throws Exception {
        Patient input = new Patient();
        input.setFirstName("Alice");

        mockMvc.perform(post("/api/patients")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "PATIENT"})
    void getAllPatients_asAuthorized_shouldReturnList() throws Exception {
        Patient p = new Patient();
        p.setId(1L);
        p.setFirstName("Alice");
        p.setLastName("Wonderland");
        p.setEmail("alice@example.com");
        p.setPhone("+1122334455");

        when(patientService.getAllPatients()).thenReturn(Collections.singletonList(p));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].phone").value("+1122334455"));

        verify(patientService).getAllPatients();
    }

    @Test
    @WithMockUser(roles = "PATIENT")
    void getPatientById_asPatient_shouldReturnPatient() throws Exception {
        Patient p = new Patient();
        p.setId(1L);
        p.setFirstName("Alice");
        p.setLastName("Wonderland");
        p.setEmail("alice@example.com");
        p.setPhone("+1122334455");

        when(patientService.getPatientById(1L)).thenReturn(p);

        mockMvc.perform(get("/api/patients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.lastName").value("Wonderland"));

        verify(patientService).getPatientById(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updatePatient_asAdmin_shouldReturnUpdated() throws Exception {
        Patient input = new Patient();
        input.setFirstName("Bob");
        input.setLastName("Builder");
        input.setEmail("bob@example.com");
        input.setPhone("+5566778899");

        Patient updated = new Patient();
        updated.setId(1L);
        updated.setFirstName("Bob");
        updated.setLastName("Builder");
        updated.setEmail("bob@example.com");
        updated.setPhone("+5566778899");

        when(patientService.updatePatient(eq(1L), any(Patient.class))).thenReturn(updated);

        mockMvc.perform(put("/api/patients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Bob"))
                .andExpect(jsonPath("$.email").value("bob@example.com"));

        verify(patientService).updatePatient(eq(1L), any(Patient.class));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePatient_asAdmin_shouldReturnNoContent() throws Exception {
        when(patientService.deletePatient(1L)).thenReturn(true);

        mockMvc.perform(delete("/api/patients/1"))
                .andExpect(status().isNoContent());

        verify(patientService).deletePatient(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deletePatient_whenNotExists_shouldReturnNotFound() throws Exception {
        when(patientService.deletePatient(2L)).thenReturn(false);

        mockMvc.perform(delete("/api/patients/2"))
                .andExpect(status().isNotFound());

        verify(patientService).deletePatient(2L);
    }
}
