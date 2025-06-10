package org.example.medreservationsystem.controller;

import org.example.medreservationsystem.dto.DoctorRequest;
import org.example.medreservationsystem.model.Doctor;
import org.example.medreservationsystem.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @PostMapping
    public ResponseEntity<Doctor> createDoctor(@Valid @RequestBody DoctorRequest req) {
        // Tworzymy encję Doctor w serwisie
        Doctor d = new Doctor();
        d.setUsername(req.getUsername());
        d.setEmail(req.getEmail());
        d.setPassword(req.getPassword());
        d.setFirstName(req.getFirstName());
        d.setLastName(req.getLastName());
        d.setSpecialization(req.getSpecialization());
        d.setPhoneNumber(req.getPhoneNumber());
        if (req.getNotificationChannel() != null) {
            d.setNotificationChannel(req.getNotificationChannel());
        }
        Doctor saved = doctorService.saveDoctor(d);
        saved.setPassword(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping
    public List<Doctor> getAllDoctors() {
        List<Doctor> list = doctorService.getAllDoctors();
        list.forEach(d -> d.setPassword(null));
        return list;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id) {
        Doctor doctor = doctorService.getDoctorById(id);
        if (doctor == null) {
            return ResponseEntity.notFound().build();
        }
        doctor.setPassword(null);
        return ResponseEntity.ok(doctor);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Doctor> updateDoctor(
            @PathVariable Long id,
            @Valid @RequestBody DoctorRequest req) {
        // Tworzymy obiekt ze zaktualizowanymi danymi
        Doctor details = new Doctor();
        details.setEmail(req.getEmail());
        details.setPassword(req.getPassword());
        details.setFirstName(req.getFirstName());
        details.setLastName(req.getLastName());
        details.setSpecialization(req.getSpecialization());
        details.setPhoneNumber(req.getPhoneNumber());
        if (req.getNotificationChannel() != null) {
            details.setNotificationChannel(req.getNotificationChannel());
        }
        Doctor updated = doctorService.updateDoctor(id, details);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        updated.setPassword(null);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        boolean deleted = doctorService.deleteDoctor(id);
        if (!deleted) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }
}
