package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.Patient;
import org.example.medreservationsystem.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    void savePatient_shouldReturnSavedPatient() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("Alice");
        patient.setLastName("Wonderland");
        patient.setEmail("alice@example.com");
        patient.setPhone("+1122334455");

        when(patientRepository.save(patient)).thenReturn(patient);

        Patient saved = patientService.savePatient(patient);

        assertThat(saved).isEqualTo(patient);
        verify(patientRepository).save(patient);
    }

    @Test
    void getAllPatients_shouldReturnList() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("Alice");
        patient.setLastName("Wonderland");
        patient.setEmail("alice@example.com");
        patient.setPhone("+1122334455");

        List<Patient> patients = Collections.singletonList(patient);
        when(patientRepository.findAll()).thenReturn(patients);

        List<Patient> result = patientService.getAllPatients();

        assertThat(result).hasSize(1).contains(patient);
        verify(patientRepository).findAll();
    }

    @Test
    void getPatientById_whenExists_shouldReturnPatient() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("Alice");
        patient.setLastName("Wonderland");
        patient.setEmail("alice@example.com");
        patient.setPhone("+1122334455");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        Patient found = patientService.getPatientById(1L);

        assertThat(found).isEqualTo(patient);
        verify(patientRepository).findById(1L);
    }

    @Test
    void getPatientById_whenNotExists_shouldReturnNull() {
        when(patientRepository.findById(2L)).thenReturn(Optional.empty());

        Patient found = patientService.getPatientById(2L);

        assertThat(found).isNull();
        verify(patientRepository).findById(2L);
    }

    @Test
    void updatePatient_whenExists_shouldUpdateAndReturn() {
        Patient existing = new Patient();
        existing.setId(1L);
        existing.setFirstName("Alice");
        existing.setLastName("Wonderland");
        existing.setEmail("alice@example.com");
        existing.setPhone("+1122334455");

        Patient details = new Patient();
        details.setFirstName("Bob");
        details.setLastName("Builder");
        details.setEmail("bob@example.com");
        details.setPhone("+5566778899");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Patient updated = patientService.updatePatient(1L, details);

        assertThat(updated.getFirstName()).isEqualTo("Bob");
        assertThat(updated.getLastName()).isEqualTo("Builder");
        assertThat(updated.getEmail()).isEqualTo("bob@example.com");
        assertThat(updated.getPhone()).isEqualTo("+5566778899");
        verify(patientRepository).findById(1L);
        verify(patientRepository).save(existing);
    }

    @Test
    void updatePatient_whenNotExists_shouldReturnNull() {
        Patient details = new Patient();
        details.setFirstName("Bob");
        details.setLastName("Builder");
        details.setEmail("bob@example.com");
        details.setPhone("+5566778899");

        when(patientRepository.findById(2L)).thenReturn(Optional.empty());

        Patient updated = patientService.updatePatient(2L, details);

        assertThat(updated).isNull();
        verify(patientRepository).findById(2L);
        verify(patientRepository, never()).save(any());
    }

    @Test
    void deletePatient_whenExists_shouldReturnTrueAndDelete() {
        Patient patient = new Patient();
        patient.setId(1L);
        patient.setFirstName("Alice");
        patient.setLastName("Wonderland");
        patient.setEmail("alice@example.com");
        patient.setPhone("+1122334455");

        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        boolean result = patientService.deletePatient(1L);

        assertThat(result).isTrue();
        verify(patientRepository).findById(1L);
        verify(patientRepository).delete(patient);
    }

    @Test
    void deletePatient_whenNotExists_shouldReturnFalse() {
        when(patientRepository.findById(2L)).thenReturn(Optional.empty());

        boolean result = patientService.deletePatient(2L);

        assertThat(result).isFalse();
        verify(patientRepository).findById(2L);
        verify(patientRepository, never()).delete(any());
    }
}
