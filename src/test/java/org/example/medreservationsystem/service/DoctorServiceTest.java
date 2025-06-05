package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.Doctor;
import org.example.medreservationsystem.repository.DoctorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorService doctorService;

    @Test
    void saveDoctor_shouldReturnSavedDoctor() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctor.setSpecialization("Cardiology");
        doctor.setEmail("john.doe@example.com");
        doctor.setPhone("+123456789");

        when(doctorRepository.save(doctor)).thenReturn(doctor);

        Doctor saved = doctorService.saveDoctor(doctor);

        assertThat(saved).isEqualTo(doctor);
        verify(doctorRepository).save(doctor);
    }

    @Test
    void getAllDoctors_shouldReturnList() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctor.setSpecialization("Cardiology");
        doctor.setEmail("john.doe@example.com");
        doctor.setPhone("+123456789");

        List<Doctor> doctors = Collections.singletonList(doctor);
        when(doctorRepository.findAll()).thenReturn(doctors);

        List<Doctor> result = doctorService.getAllDoctors();

        assertThat(result).hasSize(1).contains(doctor);
        verify(doctorRepository).findAll();
    }

    @Test
    void getDoctorById_whenExists_shouldReturnDoctor() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctor.setSpecialization("Cardiology");
        doctor.setEmail("john.doe@example.com");
        doctor.setPhone("+123456789");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        Doctor found = doctorService.getDoctorById(1L);

        assertThat(found).isEqualTo(doctor);
        verify(doctorRepository).findById(1L);
    }

    @Test
    void getDoctorById_whenNotExists_shouldReturnNull() {
        when(doctorRepository.findById(2L)).thenReturn(Optional.empty());

        Doctor found = doctorService.getDoctorById(2L);

        assertThat(found).isNull();
        verify(doctorRepository).findById(2L);
    }

    @Test
    void updateDoctor_whenExists_shouldUpdateAndReturn() {
        Doctor existing = new Doctor();
        existing.setId(1L);
        existing.setFirstName("John");
        existing.setLastName("Doe");
        existing.setSpecialization("Cardiology");
        existing.setEmail("john.doe@example.com");
        existing.setPhone("+123456789");

        Doctor details = new Doctor();
        details.setFirstName("Jane");
        details.setLastName("Smith");
        details.setSpecialization("Neurology");
        details.setEmail("jane.smith@example.com");
        details.setPhone("+987654321");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(doctorRepository.save(any(Doctor.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Doctor updated = doctorService.updateDoctor(1L, details);

        assertThat(updated.getFirstName()).isEqualTo("Jane");
        assertThat(updated.getLastName()).isEqualTo("Smith");
        assertThat(updated.getSpecialization()).isEqualTo("Neurology");
        assertThat(updated.getEmail()).isEqualTo("jane.smith@example.com");
        assertThat(updated.getPhone()).isEqualTo("+987654321");
        verify(doctorRepository).findById(1L);
        verify(doctorRepository).save(existing);
    }

    @Test
    void updateDoctor_whenNotExists_shouldReturnNull() {
        Doctor details = new Doctor();
        details.setFirstName("Jane");
        details.setLastName("Smith");
        details.setSpecialization("Neurology");
        details.setEmail("jane.smith@example.com");
        details.setPhone("+987654321");

        when(doctorRepository.findById(2L)).thenReturn(Optional.empty());

        Doctor updated = doctorService.updateDoctor(2L, details);

        assertThat(updated).isNull();
        verify(doctorRepository).findById(2L);
        verify(doctorRepository, never()).save(any());
    }

    @Test
    void deleteDoctor_whenExists_shouldReturnTrueAndDelete() {
        Doctor doctor = new Doctor();
        doctor.setId(1L);
        doctor.setFirstName("John");
        doctor.setLastName("Doe");
        doctor.setSpecialization("Cardiology");
        doctor.setEmail("john.doe@example.com");
        doctor.setPhone("+123456789");

        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        boolean result = doctorService.deleteDoctor(1L);

        assertThat(result).isTrue();
        verify(doctorRepository).findById(1L);
        verify(doctorRepository).delete(doctor);
    }

    @Test
    void deleteDoctor_whenNotExists_shouldReturnFalse() {
        when(doctorRepository.findById(2L)).thenReturn(Optional.empty());

        boolean result = doctorService.deleteDoctor(2L);

        assertThat(result).isFalse();
        verify(doctorRepository).findById(2L);
        verify(doctorRepository, never()).delete(any());
    }
}
