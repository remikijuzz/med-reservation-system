package org.example.medreservationsystem.service;

import lombok.RequiredArgsConstructor;
import org.example.medreservationsystem.model.Patient;
import org.example.medreservationsystem.repository.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository repo;

    public List<Patient> findAll() {
        return repo.findAll();
    }

    public Patient findById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Patient not found"));
    }

    public Patient create(Patient patient) {
        return repo.save(patient);
    }

    public Patient update(Long id, Patient patient) {
        Patient existing = findById(id);
        existing.setFirstName(patient.getFirstName());
        existing.setLastName(patient.getLastName());
        existing.setEmail(patient.getEmail());
        existing.setPhone(patient.getPhone());
        return repo.save(existing);
    }

    public void delete(Long id) {
        Patient existing = findById(id);
        repo.delete(existing);
    }
}
