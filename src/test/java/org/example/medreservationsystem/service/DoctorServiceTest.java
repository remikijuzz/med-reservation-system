// src/test/java/org/example/medreservationsystem/service/DoctorServiceTest.java

package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.Doctor;
import org.example.medreservationsystem.model.User;
import org.example.medreservationsystem.repository.DoctorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private DoctorService doctorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveDoctor_encodesPassword_andAddsRole() {
        // given
        Doctor doc = new Doctor();
        doc.setUsername("doc1");
        doc.setPassword("plainPw");
        // przy zapisie chcemy zwrócić obiekt przekazany do save
        when(passwordEncoder.encode("plainPw")).thenReturn("encodedPw");
        when(doctorRepository.save(any(Doctor.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        // when
        Doctor result = doctorService.saveDoctor(doc);

        // then
        assertEquals("encodedPw", result.getPassword());
        assertTrue(result.getRoles().contains(User.ROLE_DOCTOR));
        verify(passwordEncoder).encode("plainPw");
        verify(doctorRepository).save(doc);
    }

    @Test
    void getAllDoctors_returnsListFromRepo() {
        // given
        Doctor d1 = new Doctor(), d2 = new Doctor();
        when(doctorRepository.findAll()).thenReturn(List.of(d1, d2));

        // when
        List<Doctor> result = doctorService.getAllDoctors();

        // then
        assertEquals(2, result.size());
        assertSame(d1, result.get(0));
        assertSame(d2, result.get(1));
    }

    @Test
    void getDoctorById_found_returnsDoctor() {
        // given
        Doctor d = new Doctor();
        when(doctorRepository.findById(42L)).thenReturn(Optional.of(d));

        // when
        Doctor result = doctorService.getAllDoctors().contains(d) ? d : doctorService.getDoctorById(42L);

        // then
        // (poprawiamy: bezpośrednio testujemy getDoctorById)
        result = doctorService.getDoctorById(42L);
        assertSame(d, result);
    }

    @Test
    void getDoctorById_notFound_returnsNull() {
        when(doctorRepository.findById(99L)).thenReturn(Optional.empty());

        assertNull(doctorService.getDoctorById(99L));
    }

    @Test
    void updateDoctor_existing_updatesFields() {
        // given
        Doctor existing = new Doctor();
        existing.setId(1L);
        existing.setEmail("old@mail");
        existing.setFirstName("Old");
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("newPw")).thenReturn("encNewPw");
        when(doctorRepository.save(any(Doctor.class)))
            .thenAnswer(inv -> inv.getArgument(0));

        Doctor details = new Doctor();
        details.setEmail("new@mail");
        details.setPassword("newPw");
        details.setFirstName("New");
        // only email, password, firstName changed in this scenario

        // when
        Doctor result = doctorService.updateDoctor(1L, details);

        // then
        assertEquals("new@mail", result.getEmail());
        assertEquals("encNewPw", result.getPassword());
        assertEquals("New", result.getFirstName());
        verify(doctorRepository).save(existing);
    }

    @Test
    void updateDoctor_notFound_returnsNull() {
        when(doctorRepository.findById(2L)).thenReturn(Optional.empty());

        assertNull(doctorService.updateDoctor(2L, new Doctor()));
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void deleteDoctor_existing_deletesAndReturnsTrue() {
        Doctor d = new Doctor();
        d.setId(5L);
        when(doctorRepository.findById(5L)).thenReturn(Optional.of(d));

        boolean deleted = doctorService.deleteDoctor(5L);

        assertTrue(deleted);
        verify(doctorRepository).delete(d);
    }

    @Test
    void deleteDoctor_notFound_returnsFalse() {
        when(doctorRepository.findById(6L)).thenReturn(Optional.empty());

        assertFalse(doctorService.deleteDoctor(6L));
        verify(doctorRepository, never()).delete(any());
    }
}
