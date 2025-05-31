package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.Appointment;
import org.example.medreservationsystem.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final NotificationService notificationService;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                              NotificationService notificationService) {
        this.appointmentRepository = appointmentRepository;
        this.notificationService = notificationService;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long id) {
        Optional<Appointment> opt = appointmentRepository.findById(id);
        return opt.orElse(null);
    }

    public Appointment createAppointment(Appointment appointment) {
        Appointment saved = appointmentRepository.save(appointment);
        // Po zapisaniu wysyłamy powiadomienie
        notificationService.sendNotification(saved);
        return saved;
    }

    public Appointment updateAppointment(Long id, Appointment appointmentDetails) {
        return appointmentRepository.findById(id).map(appointment -> {
            appointment.setDate(appointmentDetails.getDate());
            appointment.setDoctor(appointmentDetails.getDoctor());
            appointment.setPatient(appointmentDetails.getPatient());
            Appointment updated = appointmentRepository.save(appointment);
            // Po aktualizacji również możemy powiadomić
            notificationService.sendNotification(updated);
            return updated;
        }).orElse(null);
    }

    public boolean deleteAppointment(Long id) {
        return appointmentRepository.findById(id).map(appointment -> {
            appointmentRepository.delete(appointment);
            return true;
        }).orElse(false);
    }
}
