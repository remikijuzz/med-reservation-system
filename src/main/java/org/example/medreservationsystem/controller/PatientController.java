package org.example.medreservationsystem.controller;

import org.example.medreservationsystem.dto.PatientRequest;
import org.example.medreservationsystem.model.Patient;
import org.example.medreservationsystem.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @PostMapping
    public ResponseEntity<Patient> createPatient(@Valid @RequestBody PatientRequest req) {
        Patient p = new Patient();
        p.setUsername(req.getUsername());
        p.setEmail(req.getEmail());
        p.setPassword(req.getPassword());
        p.setFirstName(req.getFirstName());
        p.setLastName(req.getLastName());
        p.setPhoneNumber(req.getPhoneNumber());
        if (req.getNotificationChannel() != null) {
            p.setNotificationChannel(req.getNotificationChannel());
        }
        Patient saved = patientService.savePatient(p);
        saved.setPassword(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<Patient> getAllPatients() {
        List<Patient> list = patientService.getAllPatients();
        list.forEach(p -> p.setPassword(null));
        return list;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        Patient patient = patientService.getPatientById(id);
        if (patient == null) {
            return ResponseEntity.notFound().build();
        }
        patient.setPassword(null);
        return ResponseEntity.ok(patient);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(
            @PathVariable Long id,
            @Valid @RequestBody PatientRequest req) {
        Patient details = new Patient();
        details.setEmail(req.getEmail());
        details.setPassword(req.getPassword());
        details.setFirstName(req.getFirstName());
        details.setLastName(req.getLastName());
        details.setPhoneNumber(req.getPhoneNumber());
        if (req.getNotificationChannel() != null) {
            details.setNotificationChannel(req.getNotificationChannel());
        }
        Patient updated = patientService.updatePatient(id, details);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        updated.setPassword(null);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        boolean deleted = patientService.deletePatient(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
