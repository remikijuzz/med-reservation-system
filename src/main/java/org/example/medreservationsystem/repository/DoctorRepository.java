package org.example.medreservationsystem.repository;

import org.example.medreservationsystem.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    // dodatkowe metody, np. findBySpecialization(...)
}
