// src/test/java/org/example/medreservationsystem/service/PatientServiceTest.java

package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.Patient;
import org.example.medreservationsystem.model.User;
import org.example.medreservationsystem.repository.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PatientService patientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void savePatient_encodesPassword_andAddsRole() {
        // given
        Patient p = new Patient();
        p.setUsername("pat1");
        p.setPassword("plainPw");

        when(passwordEncoder.encode("plainPw")).thenReturn("encodedPw");
        // zwróć tę samą instancję z save
        when(patientRepository.save(any(Patient.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        Patient result = patientService.savePatient(p);

        // then
        assertEquals("encodedPw", result.getPassword());
        assertTrue(result.getRoles().contains(User.ROLE_PATIENT));
        verify(passwordEncoder).encode("plainPw");
        verify(patientRepository).save(p);
    }

    @Test
    void getAllPatients_returnsListFromRepo() {
        // given
        Patient p1 = new Patient(), p2 = new Patient();
        when(patientRepository.findAll()).thenReturn(List.of(p1, p2));

        // when
        List<Patient> result = patientService.getAllPatients();

        // then
        assertEquals(2, result.size());
        assertSame(p1, result.get(0));
        assertSame(p2, result.get(1));
    }

    @Test
    void getPatientById_found_returnsPatient() {
        // given
        Patient p = new Patient();
        when(patientRepository.findById(10L)).thenReturn(Optional.of(p));

        // when
        Patient result = patientService.getPatientById(10L);

        // then
        assertSame(p, result);
    }

    @Test
    void getPatientById_notFound_returnsNull() {
        when(patientRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(patientService.getPatientById(99L));
    }

    @Test
    void updatePatient_existing_updatesFields() {
        // given
        Patient existing = new Patient();
        existing.setId(5L);
        existing.setEmail("old@mail");
        existing.setFirstName("Old");
        when(patientRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newPw")).thenReturn("encNewPw");
        when(patientRepository.save(any(Patient.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        Patient details = new Patient();
        details.setEmail("new@mail");
        details.setPassword("newPw");
        details.setFirstName("New");
        // leave other fields null to verify they don't change

        // when
        Patient result = patientService.updatePatient(5L, details);

        // then
        assertEquals("new@mail", result.getEmail());
        assertEquals("encNewPw", result.getPassword());
        assertEquals("New", result.getFirstName());
        verify(patientRepository).save(existing);
    }

    @Test
    void updatePatient_notFound_returnsNull() {
        when(patientRepository.findById(7L)).thenReturn(Optional.empty());

        assertNull(patientService.updatePatient(7L, new Patient()));
        verify(patientRepository, never()).save(any());
    }

    @Test
    void deletePatient_existing_deletesAndReturnsTrue() {
        // given
        Patient p = new Patient();
        p.setId(8L);
        when(patientRepository.findById(8L)).thenReturn(Optional.of(p));

        // when
        boolean deleted = patientService.deletePatient(8L);

        // then
        assertTrue(deleted);
        verify(patientRepository).delete(p);
    }

    @Test
    void deletePatient_notFound_returnsFalse() {
        when(patientRepository.findById(11L)).thenReturn(Optional.empty());

        assertFalse(patientService.deletePatient(11L));
        verify(patientRepository, never()).delete(any());
    }
}
