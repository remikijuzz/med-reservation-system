package org.example.medreservationsystem.repository;

import org.example.medreservationsystem.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> { }
