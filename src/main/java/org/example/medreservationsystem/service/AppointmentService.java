package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.Appointment;
import org.example.medreservationsystem.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Appointment saveAppointment(Appointment appointment) {
        Appointment saved = appointmentRepository.save(appointment);
        notificationService.sendNotification(saved);
        return saved;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    public Appointment updateAppointment(Long id, Appointment appointmentDetails) {
        return appointmentRepository.findById(id).map(appointment -> {
            appointment.setDate(appointmentDetails.getDate());
            appointment.setDoctor(appointmentDetails.getDoctor());
            appointment.setPatient(appointmentDetails.getPatient());
            // Jeśli masz status, odkomentuj:
//            appointment.setStatus(appointmentDetails.getStatus());
            Appointment updated = appointmentRepository.save(appointment);
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
