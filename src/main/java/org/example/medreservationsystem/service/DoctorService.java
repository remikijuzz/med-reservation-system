package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.Doctor;
import org.example.medreservationsystem.repository.DoctorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final PasswordEncoder passwordEncoder;

    public DoctorService(DoctorRepository doctorRepository,
                         PasswordEncoder passwordEncoder) {
        this.doctorRepository = doctorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Doctor saveDoctor(Doctor doctor) {
        // Jeśli hasło jest w plain text, zakoduj je; jeśli już zakodowane, można pominąć
        doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
        doctor.getRoles().add(org.example.medreservationsystem.model.User.ROLE_DOCTOR);
        return doctorRepository.save(doctor);
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id).orElse(null);
    }

    public Doctor updateDoctor(Long id, Doctor doctorDetails) {
        return doctorRepository.findById(id).map(doctor -> {
            // Przykładowa aktualizacja: jeżeli pola nie są null, nadpisz
            if (doctorDetails.getEmail() != null) {
                doctor.setEmail(doctorDetails.getEmail());
            }
            if (doctorDetails.getPassword() != null) {
                doctor.setPassword(passwordEncoder.encode(doctorDetails.getPassword()));
            }
            if (doctorDetails.getFirstName() != null) {
                doctor.setFirstName(doctorDetails.getFirstName());
            }
            if (doctorDetails.getLastName() != null) {
                doctor.setLastName(doctorDetails.getLastName());
            }
            if (doctorDetails.getSpecialization() != null) {
                doctor.setSpecialization(doctorDetails.getSpecialization());
            }
            if (doctorDetails.getPhoneNumber() != null) {
                doctor.setPhoneNumber(doctorDetails.getPhoneNumber());
            }
            if (doctorDetails.getNotificationChannel()!=null) {
                doctor.setNotificationChannel(doctorDetails.getNotificationChannel());
            }
            return doctorRepository.save(doctor);
        }).orElse(null);
    }

    public boolean deleteDoctor(Long id) {
        return doctorRepository.findById(id).map(doctor -> {
            doctorRepository.delete(doctor);
            return true;
        }).orElse(false);
    }
}
