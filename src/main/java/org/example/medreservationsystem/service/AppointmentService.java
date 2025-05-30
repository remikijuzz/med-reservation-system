package org.example.medreservationsystem.service;

import lombok.RequiredArgsConstructor;
import org.example.medreservationsystem.model.Appointment;
import org.example.medreservationsystem.repository.AppointmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository repo;

    public List<Appointment> findAll() {
        return repo.findAll();
    }

    public Appointment findById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));
    }

    public Appointment create(Appointment appointment) {
        return repo.save(appointment);
    }

    public Appointment update(Long id, Appointment appointment) {
        Appointment existing = findById(id);
        existing.setAppointmentTime(appointment.getAppointmentTime());
        existing.setStatus(appointment.getStatus());
        existing.setPatient(appointment.getPatient());
        existing.setDoctor(appointment.getDoctor());
        return repo.save(existing);
    }

    public void delete(Long id) {
        Appointment existing = findById(id);
        repo.delete(existing);
    }
}
