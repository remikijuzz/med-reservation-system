package org.example.medreservationsystem.repository;

import org.example.medreservationsystem.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> { }
