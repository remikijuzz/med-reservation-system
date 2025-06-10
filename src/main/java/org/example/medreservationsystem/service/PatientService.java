package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.Patient;
import org.example.medreservationsystem.repository.PatientRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;

    public PatientService(PatientRepository patientRepository,
                          PasswordEncoder passwordEncoder) {
        this.patientRepository = patientRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Patient savePatient(Patient patient) {
        patient.setPassword(passwordEncoder.encode(patient.getPassword()));
        patient.getRoles().add(org.example.medreservationsystem.model.User.ROLE_PATIENT);
        return patientRepository.save(patient);
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(Long id) {
        return patientRepository.findById(id).orElse(null);
    }

    public Patient updatePatient(Long id, Patient patientDetails) {
        return patientRepository.findById(id).map(patient -> {
            if (patientDetails.getEmail() != null) {
                patient.setEmail(patientDetails.getEmail());
            }
            if (patientDetails.getPassword() != null) {
                patient.setPassword(passwordEncoder.encode(patientDetails.getPassword()));
            }
            if (patientDetails.getFirstName() != null) {
                patient.setFirstName(patientDetails.getFirstName());
            }
            if (patientDetails.getLastName() != null) {
                patient.setLastName(patientDetails.getLastName());
            }
            if (patientDetails.getPhoneNumber() != null) {
                patient.setPhoneNumber(patientDetails.getPhoneNumber());
            }
            if (patientDetails.getNotificationChannel()!=null) {
                patient.setNotificationChannel(patientDetails.getNotificationChannel());
            }
            return patientRepository.save(patient);
        }).orElse(null);
    }

    public boolean deletePatient(Long id) {
        return patientRepository.findById(id).map(patient -> {
            patientRepository.delete(patient);
            return true;
        }).orElse(false);
    }
}
