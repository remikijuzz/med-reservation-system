package org.example.medreservationsystem.service;

import lombok.RequiredArgsConstructor;
import org.example.medreservationsystem.model.Doctor;
import org.example.medreservationsystem.repository.DoctorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorService {
    private final DoctorRepository repo;

    public List<Doctor> findAll() {
        return repo.findAll();
    }

    public Doctor findById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Doctor not found"));
    }

    public Doctor create(Doctor doctor) {
        return repo.save(doctor);
    }

    public Doctor update(Long id, Doctor doctor) {
        Doctor existing = findById(id);
        existing.setFirstName(doctor.getFirstName());
        existing.setLastName(doctor.getLastName());
        existing.setSpecialty(doctor.getSpecialty());
        existing.setEmail(doctor.getEmail());
        existing.setPhone(doctor.getPhone());
        return repo.save(existing);
    }

    public void delete(Long id) {
        Doctor existing = findById(id);
        repo.delete(existing);
    }
}
