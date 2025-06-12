package org.example.medreservationsystem.controller;

import org.example.medreservationsystem.dto.AppointmentRequest;
import org.example.medreservationsystem.model.Appointment;
import org.example.medreservationsystem.model.User;
import org.example.medreservationsystem.repository.UserRepository;
import org.example.medreservationsystem.service.AppointmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final UserRepository    userRepository;

    public AppointmentController(AppointmentService appointmentService,
                                 UserRepository userRepository) {
        this.appointmentService = appointmentService;
        this.userRepository    = userRepository;
    }

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(
            @Valid @RequestBody AppointmentRequest req) {
        Appointment saved = appointmentService.createAppointment(
                req.getPatientId(),
                req.getDoctorId(),
                req.getAppointmentDateTime(),
                req.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {
        // tylko ADMIN (już sprawdzone w SecurityConfig)
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(@PathVariable Long id) {
        Appointment appt = appointmentService.getAppointmentById(id);
        if (appt == null) {
            return ResponseEntity.notFound().build();
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isDoctor = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));
        boolean isPatient = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_PATIENT"));

        if (isAdmin) {
            // admin widzi wszystko
        } else if (isDoctor) {
            if (!appt.getDoctor().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else if (isPatient) {
            if (!appt.getPatient().getUsername().equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(appt);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(
            @PathVariable Long id,
            @Valid @RequestBody AppointmentRequest req) {
        Appointment existing = appointmentService.getAppointmentById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        // tylko PACIENT może aktualizować swoją wizytę
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!existing.getPatient().getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        Appointment updated = appointmentService.updateAppointment(
                id,
                req.getPatientId(),
                req.getDoctorId(),
                req.getAppointmentDateTime(),
                req.getDescription());
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable Long id) {
        Appointment existing = appointmentService.getAppointmentById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        String username = auth.getName();

        if (!isAdmin && !existing.getPatient().getUsername().equals(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getByPatient(@PathVariable Long patientId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdminOrDoctor = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")
                        || a.getAuthority().equals("ROLE_DOCTOR"));
        if (!isAdminOrDoctor) {
            User user = userRepository.findByUsername(username);
            if (user == null || !user.getId().equals(patientId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        }
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatient(patientId));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getByDoctor(@PathVariable Long doctorId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        boolean isDoctor = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"));

        if (isAdmin) {
            // admin widzi wszystkie
            return ResponseEntity.ok(appointmentService.getAppointmentsByDoctor(doctorId));
        }

        if (isDoctor) {
            User user = userRepository.findByUsername(username);
            if (user == null || !user.getId().equals(doctorId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
            return ResponseEntity.ok(appointmentService.getAppointmentsByDoctor(doctorId));
        }

        // pozostali nie mają dostępu
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
