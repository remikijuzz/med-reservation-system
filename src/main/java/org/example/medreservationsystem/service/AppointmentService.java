package org.example.medreservationsystem.service;

import org.example.medreservationsystem.model.Appointment;
import org.example.medreservationsystem.model.Doctor;
import org.example.medreservationsystem.model.Patient;
import org.example.medreservationsystem.notification.NotificationService;
import org.example.medreservationsystem.repository.AppointmentRepository;
import org.example.medreservationsystem.repository.DoctorRepository;
import org.example.medreservationsystem.repository.PatientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final Map<String, NotificationService> notificationServices;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository,
                              Map<String, NotificationService> notificationServices) {
        this.appointmentRepository = appointmentRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.notificationServices = notificationServices;
    }

    public Appointment createAppointment(Long patientId, Long doctorId,
                                         java.time.LocalDateTime dateTime,
                                         String description) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono pacjenta o id " + patientId));
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Nie znaleziono lekarza o id " + doctorId));

        Appointment appt = new Appointment();
        appt.setPatient(patient);
        appt.setDoctor(doctor);
        appt.setAppointmentDateTime(dateTime);
        appt.setDescription(description);

        Appointment saved = appointmentRepository.save(appt);

        // Polimorficzne powiadomienie – wysyłamy do pacjenta:
        String message = "Twoja wizyta z dr " + doctor.getLastName() +
                " zaplanowana na: " + dateTime.toString();
        String channelKey = patient.getNotificationChannel().name(); // "EMAIL" lub "SMS"
        NotificationService notifier = notificationServices.get(channelKey);
        if (notifier != null) {
            notifier.sendNotification(patient, message);
        }

        return saved;
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    public Appointment updateAppointment(Long id, Long patientId, Long doctorId,
                                         java.time.LocalDateTime dateTime,
                                         String description) {
        return appointmentRepository.findById(id).map(appt -> {
            if (patientId != null) {
                Patient patient = patientRepository.findById(patientId)
                        .orElseThrow(() -> new RuntimeException("Nie znaleziono pacjenta o id " + patientId));
                appt.setPatient(patient);
            }
            if (doctorId != null) {
                Doctor doctor = doctorRepository.findById(doctorId)
                        .orElseThrow(() -> new RuntimeException("Nie znaleziono lekarza o id " + doctorId));
                appt.setDoctor(doctor);
            }
            if (dateTime != null) {
                appt.setAppointmentDateTime(dateTime);
            }
            if (description != null) {
                appt.setDescription(description);
            }
            Appointment updated = appointmentRepository.save(appt);

            // Powiadomienie o zmianie (opcja):
            String message = "Twoja wizyta została zaktualizowana: dr " +
                    appt.getDoctor().getLastName() + ", na: " + appt.getAppointmentDateTime().toString();
            String channelKey = appt.getPatient().getNotificationChannel().name();
            NotificationService notifier = notificationServices.get(channelKey);
            if (notifier != null) {
                notifier.sendNotification(appt.getPatient(), message);
            }

            return updated;
        }).orElse(null);
    }

    public boolean deleteAppointment(Long id) {
        return appointmentRepository.findById(id).map(appt -> {
            // Przed usunięciem można wysłać powiadomienie o odwołaniu:
            String message = "Twoja wizyta z dr " +
                    appt.getDoctor().getLastName() + " dnia " +
                    appt.getAppointmentDateTime().toString() + " została odwołana.";
            String channelKey = appt.getPatient().getNotificationChannel().name();
            NotificationService notifier = notificationServices.get(channelKey);
            if (notifier != null) {
                notifier.sendNotification(appt.getPatient(), message);
            }

            appointmentRepository.delete(appt);
            return true;
        }).orElse(false);
    }

    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepository.findByPatientId(patientId);
    }

    public List<Appointment> getAppointmentsByDoctor(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }
}
